## What's new in plus-13

### Rebased on upstream `main` of 28 Oct 2026
- **Dolby Vision Profile 7 → 8.1 conversion is now upstream's**
  ([#2017](https://github.com/damontecres/Wholphin/pull/2017), started in this fork): experimental
  setting "Convert Dolby Vision Profile 7 to 8.1", off by default
- **Playlist page reworked upstream** ([#2113](https://github.com/damontecres/Wholphin/pull/2113)):
  layouts like a library tab (grid, list, backdrop), logos, a random button, remove from playlist.
  It replaces this fork's own playlist grid; the fork keeps its fixes on top — filters filled from
  the playlist's items, and movies no longer replaced by their collections
- Max bitrate can be changed during playback
  ([#2097](https://github.com/damontecres/Wholphin/pull/2097)), Wholphin server plugin support

Everything else from plus-12 is still included. The full list of differences from upstream is in the
[README](https://github.com/SergeiAK/Wholphin-plus#what-is-different-from-upstream).
