## What's new in plus-12

### Tracks remembered per season and series
Rebased on upstream `main` of 16 Oct 2026, which brings
[#1802](https://github.com/damontecres/Wholphin/pull/1802): the audio and subtitle track you pick
in an episode is remembered for its **season** and its **series**. The next episode takes the
episode's own choice first, then the season's, then the series', then your language settings.
The match is by language, then position among the tracks, channels and title, so "the second
Russian track, 5.1" carries over when the episodes have their tracks in the same order. A track
with no language set is not remembered.

### Fixed
- A subtitle chosen for one episode was **not used for the next one in the player** (the episode
  page showed it, the player fell back to the profile settings): the player looked the subtitle
  choice up as an audio one
- With several tracks in one language (English, Russian, Russian), a choice of the **second
  Russian track** came back as the first one in the next episode unless the track titles differed

Everything from plus-11 is still included. The full list of differences from upstream is in the
[README](https://github.com/SergeiAK/Wholphin-plus#what-is-different-from-upstream).
