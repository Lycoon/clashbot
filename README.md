![Clashbot banner](/src/main/resources/icons/clashbot-banner.png)

Clashbot is a verified Discord bot generating pictures from Clash of Clans ingame info. Invite it by clicking [here](https://discord.com/api/oauth2/authorize?client_id=734481969630543883&permissions=2147780672&scope=bot).

# Features
The bot proposes several features including: language choice, saving default tags, accessing clan and player profiles, clan warlogs, current clanwars, warleague rounds...

### Miscellaneous
- `!stats` - shows various data about bot.
- `!invite` - shows bot's invite link.
- `!lang` - shows your current language.
- `!info` - shows bot information.
- `!help` - shows commands and their usage.

### Settings
- `!clear` - deletes all the data the bot database has about you.
- `!set <clan|player> <tag>` sets your default clan or player tag.
- `!set lang <language>` - sets your default language.
- `!set prefix <prefix>` - sets the default server prefix.

### Clan
- `!clan [clanTag]` - shows clan profile.
- `!warleague round <index> [clanTag]` - shows specified warleague round.
- `!warlog <index> [clanTag]` - shows clan's warlog.
- `!war <index> [clanTag]` - shows current war occurring in the clan.

### Player
- `!player [playerTag]` - shows player profile

# Related
The bot relies on a custom Java wrapper I designed to make calls to the game API. I wrote it for the purpose of this project but it can be used for any other. I'm trying to keep up with the updates of the game so that most features are implemented.

# Disclaimer
This repository **does not** accept any contribution and is only consultable for the sake of transparency.
It is **strictly forbidden** to impersonate the official bot by making it run on your own.

*This material is unofficial and is not endorsed by Supercell. For more information see Supercell's Fan Content Policy: www.supercell.com/fan-content-policy.*
