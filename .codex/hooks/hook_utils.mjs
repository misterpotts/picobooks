#!/usr/bin/env node
import { createHash } from "node:crypto";
import { existsSync, readdirSync, readFileSync, statSync } from "node:fs";
import { join, relative } from "node:path";
import { spawnSync } from "node:child_process";

export const WATCHED_SUFFIXES = [".java", ".xml", ".yaml", ".yml", ".md", ".mjs", ".js", ".cjs", ".sh", ".toml", ".rules"];

export async function readEvent() {
  const chunks = [];
  for await (const chunk of process.stdin) {
    chunks.push(chunk);
  }
  const raw = Buffer.concat(chunks).toString("utf8");
  return raw.trim() ? JSON.parse(raw) : {};
}

export function writeJson(payload) {
  process.stdout.write(`${JSON.stringify(payload)}\n`);
}

export function denyPreToolUse(reason) {
  writeJson({
    hookSpecificOutput: {
      hookEventName: "PreToolUse",
      permissionDecision: "deny",
      permissionDecisionReason: reason,
    },
  });
}

export function denyPermissionRequest(reason) {
  writeJson({
    hookSpecificOutput: {
      hookEventName: "PermissionRequest",
      decision: {
        behavior: "deny",
        message: reason,
      },
    },
  });
}

export function additionalContext(hookEventName, message) {
  writeJson({
    hookSpecificOutput: {
      hookEventName,
      additionalContext: message,
    },
  });
}

export function blockStop(reason) {
  writeJson({ decision: "block", reason });
}

export function git(args, cwd) {
  return spawnSync("git", args, {
    cwd,
    encoding: "utf8",
    windowsHide: true,
  });
}

export function inGitRepo(cwd) {
  const result = git(["rev-parse", "--is-inside-work-tree"], cwd);
  return result.status === 0 && result.stdout.trim() === "true";
}

export function commandFromEvent(event) {
  return String(event?.tool_input?.command ?? "");
}

export function tokenizeCommandSegment(segment) {
  const tokens = [];
  const pattern = /"([^"\\]*(?:\\.[^"\\]*)*)"|'([^']*)'|(\S+)/g;
  let match;
  while ((match = pattern.exec(segment)) !== null) {
    tokens.push(match[1] ?? match[2] ?? match[3]);
  }
  return tokens;
}

export function commandSegments(command) {
  return command
    .split(/&&|\|\||[;|]/)
    .map((segment) => segment.trim())
    .filter(Boolean);
}

function isMavenExecutable(token) {
  const normalized = token.replaceAll("\\", "/").toLowerCase();
  return ["mvn", "mvn.cmd", "mvnw", "mvnw.cmd", "./mvnw", "./mvnw.cmd"].includes(normalized);
}

function skipsTests(tokens) {
  return tokens.some((token) => {
    const match = token.match(/^-D(skipTests|maven\.test\.skip)(?:=(.*))?$/i);
    if (!match) {
      return false;
    }
    const value = match[2];
    return value === undefined || !["false", "0", "no"].includes(value.toLowerCase());
  });
}

export function isMavenVerifyCommand(command) {
  return commandSegments(command).some((segment) => {
    const tokens = tokenizeCommandSegment(segment);
    if (tokens.length === 0 || !isMavenExecutable(tokens[0])) {
      return false;
    }
    return tokens.includes("verify") && !skipsTests(tokens);
  });
}

export function isConventionalCommitTitle(value) {
  return /^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test)(\([a-z0-9-]+\))?!?: [^\s].+/.test(value.trim());
}

export function firstOptionValue(tokens, option) {
  for (let index = 0; index < tokens.length; index += 1) {
    const token = tokens[index];
    if (token === option) {
      return tokens[index + 1] ?? "";
    }
    if (token.startsWith(`${option}=`)) {
      return token.slice(option.length + 1);
    }
  }
  return "";
}

export function quotedFlagValues(command, flag) {
  const escapedFlag = flag.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  const values = [];
  const pattern = new RegExp(`${escapedFlag}\\s+(?:"([^"]*)"|'([^']*)'|([^\\s]+))`, "g");
  let match;
  while ((match = pattern.exec(command)) !== null) {
    values.push(match[1] ?? match[2] ?? match[3] ?? "");
  }
  return values;
}

export function changedFiles(cwd) {
  const result = git(["status", "--porcelain=v1"], cwd);
  if (result.status !== 0) {
    return [];
  }
  const files = [];
  for (const line of result.stdout.split(/\r?\n/)) {
    if (!line.trim()) {
      continue;
    }
    const pathPart = line.slice(3).trim();
    if (pathPart.includes(" -> ")) {
      files.push(...pathPart.split(" -> ").map((path) => path.trim()));
    } else {
      files.push(pathPart);
    }
  }
  return files;
}

export function isRelevantFile(path) {
  return WATCHED_SUFFIXES.some((suffix) => path.endsWith(suffix));
}

export function relevantChangedFiles(cwd) {
  return changedFiles(cwd).filter(isRelevantFile).sort();
}

function sha256(value) {
  return createHash("sha256").update(value).digest("hex");
}

function fileHash(cwd, file) {
  const path = join(cwd, file);
  if (!existsSync(path)) {
    return "<missing>";
  }
  const stats = statSync(path);
  if (!stats.isFile()) {
    return "<not-file>";
  }
  return sha256(readFileSync(path));
}

export function workspaceFingerprint(cwd) {
  const head = git(["rev-parse", "HEAD"], cwd).stdout.trim();
  const status = git(["status", "--porcelain=v1"], cwd).stdout;
  const relevantFiles = relevantChangedFiles(cwd);
  const relevantContent = relevantFiles.map((file) => [file, fileHash(cwd, file)]);
  return sha256(JSON.stringify({ head, status, relevantContent }));
}

export function trackedOpenSpecChanges(cwd) {
  const result = git(["ls-files", "openspec/changes"], cwd);
  return result.status === 0 && result.stdout.trim().length > 0;
}

export function hasTrackedOpenSpecChanges(cwd) {
  return trackedOpenSpecChanges(cwd);
}

function readIfExists(path) {
  return existsSync(path) ? readFileSync(path, "utf8") : "";
}

function listFiles(root, predicate, output = []) {
  if (!existsSync(root)) {
    return output;
  }
  for (const entry of readdirSync(root, { withFileTypes: true })) {
    const path = join(root, entry.name);
    if (entry.isDirectory()) {
      listFiles(path, predicate, output);
    } else if (entry.isFile() && predicate(path)) {
      output.push(path);
    }
  }
  return output;
}

export function repositoryConstraintViolations(cwd) {
  const violations = [];
  const pomPath = join(cwd, "pom.xml");
  const pom = readIfExists(pomPath);
  const applicationYaml = readIfExists(join(cwd, "src", "main", "resources", "application.yaml"));
  const applicationProperties = readIfExists(join(cwd, "src", "main", "resources", "application.properties"));
  const hasApplicationFiles = pom.length > 0 || applicationYaml.length > 0 || applicationProperties.length > 0;

  if (!hasApplicationFiles) {
    return violations;
  }

  if (!/<java\.version>\s*25\s*<\/java\.version>/.test(pom)) {
    violations.push("pom.xml must keep `<java.version>25</java.version>`.");
  }

  if (!/<artifactId>\s*spring-boot-starter-parent\s*<\/artifactId>\s*[\s\S]*?<version>\s*3\.5\.[^<]+<\/version>/.test(pom)) {
    violations.push("Spring Boot must remain on the 3.5.x line.");
  }

  if (!/<artifactId>\s*spring-boot-starter-web\s*<\/artifactId>/.test(pom)) {
    violations.push("pom.xml must keep the Spring MVC servlet starter.");
  }

  if (/spring-boot-starter-webflux|reactor-core/i.test(pom)) {
    violations.push("pom.xml must not introduce WebFlux or Reactor dependencies.");
  }

  const javaFiles = listFiles(join(cwd, "src"), (path) => path.endsWith(".java"));
  for (const file of javaFiles) {
    const text = readIfExists(file);
    if (/\borg\.springframework\.web\.reactive\b|\breactor\./.test(text)) {
      violations.push(`${relative(cwd, file)} imports reactive/WebFlux APIs.`);
    }
  }

  const propertiesEnableVirtualThreads = /spring\.threads\.virtual\.enabled\s*=\s*true/i.test(applicationProperties);
  const yamlEnablesVirtualThreads = /spring:\s*[\s\S]*?threads:\s*[\s\S]*?virtual:\s*[\s\S]*?enabled:\s*true/i.test(applicationYaml);
  if (!propertiesEnableVirtualThreads && !yamlEnablesVirtualThreads) {
    violations.push("Virtual threads must stay enabled with `spring.threads.virtual.enabled=true`.");
  }

  if (/web-application-type\s*:\s*(?!servlet\b)\S+/i.test(applicationYaml)) {
    violations.push("Spring must remain configured as a servlet application, not reactive.");
  }

  return violations;
}
