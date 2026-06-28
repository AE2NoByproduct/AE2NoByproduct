# Releasing AE2 No Byproduct

Publishing is automated. When you publish a GitHub Release, the [`release.yml`](.github/workflows/release.yml) workflow builds **both loader jars** (Forge + Fabric) and publishes them to the GitHub Release, CurseForge, and Modrinth. The CurseForge id, Modrinth slug, and API tokens are already configured. See [How publishing works](#how-publishing-works) for the per-loader details.

## Cutting a release

1. Make sure `main` builds: `./gradlew build` (tests pass).
2. Confirm `README.md` and `CURSEFORGE.md` are aligned and contain no em-dashes.
3. Bump `mod_version` in `gradle.properties` (semver). The git tag will be `v<mod_version>`, and the published game version is read from `minecraft_version` in the same file.
4. Update `CHANGELOG.md`: move the `[Unreleased]` items into a new `## [x.y.z] - YYYY-MM-DD` section, refresh the compare links, and leave a fresh empty `[Unreleased]`.
5. Commit and push to `main`.
6. On GitHub, go to **Releases → Draft a new release**:
   - Tag `vx.y.z` (matching `mod_version`).
   - Click **Generate release notes** for the body, or paste the `CHANGELOG.md` section.
   - **Publish release.**
7. Watch the **Actions** tab. The `release.yml` run builds and publishes both jars to GitHub, CurseForge, and Modrinth with that changelog.

## How publishing works

`release.yml` builds every module, then publishes in **independent steps** (each with `if: !cancelled()`, so one store failing does not block the others; the job still reports failure if any step fails). GitHub runs first because it is the canonical download.

- **GitHub Release:** one step attaches BOTH jars. GitHub assets have no loader tags, so a single combined step is correct.
- **CurseForge:** one step **per loader**. CurseForge has no multi-loader "version": each file is independent and carries its own loader tag. A single call with both jars mis-groups them (the Fabric jar gets tagged Forge and only one file shows), so each loader's jar is uploaded on its own as `... (Forge)` / `... (Fabric)`.
- **Modrinth:** one step **per loader**, for the same limitation (a single multi-file call drops the second loader's jar). Each loader becomes its own version with a loader-suffixed number (`x.y.z+forge` / `x.y.z+fabric`) so the two never collide. Modrinth itself supports one version listing multiple loaders, but mc-publish cannot produce that from two separate-loader jars; merge them by hand in the Modrinth UI only if you want a single combined version.

**Adding a loader (e.g. NeoForge):** copy an existing `Publish <Loader> to CurseForge` + `Publish <Loader> to Modrinth` pair, swap the module path and the `loaders:` value, and add the new module's jar glob to the GitHub step. Never reintroduce a single all-loaders `loaders: forge\nfabric` input on a multi-file call: that is exactly what mis-tags the jars.

## GitHub-only release (skip CurseForge / Modrinth)

If that version is already on CurseForge and Modrinth (or you only want it attached to the GitHub Release), put `[skip-stores]` anywhere in the release notes. The workflow still builds and attaches both jars to the GitHub Release, but skips both stores. Handy for backfilling a GitHub Release for a version that was uploaded to the stores manually.

## Keeping the published metadata correct

- **Version match is enforced.** Before building or publishing, the release workflow fails if the git tag does not match `gradle.properties` `mod_version` (ignoring a leading `v`). The published file and the release label are therefore always the same version.
- **Game version** is read automatically from `gradle.properties` (`minecraft_version`), so it always matches what was built. No manual edit needed.
- **Loaders** are tagged per file: each store gets one publish step per loader with an explicit single `loaders:` value (see [How publishing works](#how-publishing-works)), so the Forge jar is tagged Forge and the Fabric jar is tagged Fabric. The jars carry the loader and MC version in their filename (`ae2nobyproduct-<loader>-<mcversion>-<modversion>.jar`). When adding a loader (NeoForge), add its per-loader publish steps and re-verify the Mixin targets for the new AE2 version. See [AGENTS.md](AGENTS.md).

## After publishing

Verify the file appears on the [Releases page](https://github.com/AE2NoByproduct/AE2NoByproduct/releases), on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ae2-no-byproduct), and on [Modrinth](https://modrinth.com/mod/ae2-no-byproduct).
