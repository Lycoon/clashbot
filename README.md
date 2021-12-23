![Clashbot banner](/src/main/resources/icons/clashbot-banner.png)

<p align="center">
  <a href="https://github.com/Lycoon/clashbot/actions/workflows/check-ci.yml">
    <img alt="Check CI status" src="https://github.com/Lycoon/clashbot/actions/workflows/check-ci.yml/badge.svg?branch=dev">
  </a>
  <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/Lycoon/clashbot">
</p>

**Clashbot** is a verified Discord bot generating pictures from Clash of Clans ingame info. Invite it by clicking [here](https://discord.com/api/oauth2/authorize?client_id=734481969630543883&permissions=2147780672&scope=bot).

# Features
The bot proposes several features including: language choice, saving default tags, accessing clan and player profiles, clan warlogs, current clanwars, warleague rounds...

### Miscellaneous
- `/invite` - shows bot's invite link.
- `/lang` - shows your current language.
- `/info` - shows bot information.
- `/help` - shows commands and their usage.

### Settings
- `/clear` - deletes all the data the bot database has about you.
- `/set clan <clanTag>` sets your default clan tag.
- `/set player <playerTag>` sets your default player tag.
- `/set lang <language>` - sets your default language.

### Clan
- `/clan [clanTag]` - shows clan profile.
- `/warleague <page> [clanTag]` - shows specified warleague round.
- `/warlog <page> [clanTag]` - shows clan's warlog.
- `/war <page> [clanTag]` - shows current war occurring in the clan.

### Player
- `/player [playerTag]` - shows player profile

# Related
The bot relies on a custom Java wrapper I designed to make calls to the game API. I wrote it for the purpose of this project but it can be used for any other. I'm trying to keep up with the updates of the game so that most features are implemented.

# Disclaimer
All rights reserved. This repository **does not** accept any contribution and is only consultable for the sake of transparency.
It is **strictly forbidden** to impersonate the official bot by making it run on your own.

*This material is unofficial and is not endorsed by Supercell. For more information see Supercell's Fan Content Policy: www.supercell.com/fan-content-policy.*
