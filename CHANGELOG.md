# Changelog

All notable changes to AE2 No Byproduct are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [0.1.0] - 2026-06-27

### Added
- Per-player toggle button in the AE2 Pattern Encoding Terminal that strips byproducts from processing patterns at encode time, keeping only the first output. Server-authoritative, and the choice is persisted across relog, server restart, and death.
- Server config: `enableFeature`, `allowPlayerToggle`, `defaultStrip`, `consumeOnUse`, `showMessages`. Pack makers can force byproduct stripping for everyone with no per-player UI.
- Byproduct Remover item: shift + right-click a Pattern Provider to strip byproducts from every processing pattern stored inside it. Works on base AE2 providers and add-on providers (tested with Extended AE and MEGA Cells).
- Crafting recipe for the Byproduct Remover: 16k Storage Component + Blank Pattern + Crafting Unit (shapeless).

[Unreleased]: https://github.com/MrErikCodes/AE2NoByProduct/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/MrErikCodes/AE2NoByProduct/releases/tag/v0.1.0
