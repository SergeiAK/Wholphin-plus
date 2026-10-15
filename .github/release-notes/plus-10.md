## What's new in plus-10

### One person page, wherever you open it from
An actor opened from a Seerr (Discover) title used to get a bare `Discover: <name>` grid, while the
same actor opened from a library title got the full page. Now:

- a person who is in your library opens the **regular person page** (photo, biography, library rows)
- anyone else gets the **same page filled from Seerr** — photo, biography, birth and death dates,
  place of birth and the filmography row; "View more" still opens the full grid

### Player
- **"Ends at"** is lined up with the remaining time above it and set semi-bold, same color
- **Smaller trickplay preview** while seeking — 112 dp high instead of 160 dp (half the area)

### Upstream
Rebased on the latest upstream `main`, including
[#1644](https://github.com/damontecres/Wholphin/pull/1644): new per-format settings to force
enable or disable HDR10, HDR10+ and Dolby Vision profiles 5/7/8. The fork's Dolby Vision Profile 7
→ 8.1 conversion works with them: while the conversion is on, profile 7 counts as supported unless
you force it off.

Everything from plus-9 is still included. The full list of differences from upstream is in the
[README](https://github.com/SergeiAK/Wholphin-plus#what-is-different-from-upstream).
