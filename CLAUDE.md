# CLAUDE.md

Instructions for Claude Code (and other AI assistants) in this repo.

**Read [AGENTS.md](AGENTS.md) first.** It is the source of truth for build commands, the release process, versioning, the changelog, style rules, and the pre-release checklist. Everything below is a short reminder layer on top of it.

## Always remember
- **No em-dashes** (Unicode U+2014, the long dash) anywhere. Use commas, colons, parentheses, or new sentences. Verify with `grep -rP '\x{2014}' --include='*.md' .` before finishing docs work.
- **`README.md` and `CURSEFORGE.md` must stay aligned.** If you change a feature, recipe, config option, or supported version, update BOTH.
- This is an **AE2 add-on**: when touching anything that targets AE2 internals (Mixins, the Byproduct Remover's pattern access), consult the AE2 source for the exact version, since signatures drift between versions.

## Before claiming a release is ready
Run the **Pre-release checklist in [AGENTS.md](AGENTS.md)**: build green, `mod_version` bumped to match the tag, `CHANGELOG.md` updated, docs aligned and em-dash-free, supported-versions tables current, and `.github/workflows/release.yml` (`loaders` / `game-versions` / `files`) matching what the jar actually supports.

## After changing the build, loaders, or MC versions
Update `.github/workflows/release.yml`, the supported-versions tables in both docs, and the matrix note in `AGENTS.md`. Re-verify the Mixin targets against the new AE2 version.

## Workflow
- Branch from `main`, keep changes focused, open a PR (CodeRabbit reviews PRs automatically).
- Verify in-game with `./gradlew runClient` when behavior changes; visual/GUI confirmation is the human's job.
