# Pocket Dimension

![In inventory](https://cdn.modrinth.com/data/cached_images/2958cd375be254b7f4324791441e3e8f86ac1092.png)

A Fabric mod for Minecraft that gives players access to their own personal, portable dimension. Carry your base, storage room, or safe haven with you wherever you go!

## Features

- **Personal Pocket Dimension:** Each player gets a unique, instanced room they can access by placing and interacting with the `Pocket` block.
- **Secure Access:** Pocket blocks are bound to the player who places them. Other players cannot enter your dimension through your block.
- **One Active Entrance:** You can only have one active entrance to your pocket dimension at a time. Trying to place another will simply drop the block.
- **Dynamic Relocation:** If a Pocket block is destroyed in the Overworld while you or your entities are inside, the mod safely relocates everyone. Non-player entities are teleported out, and players are dropped into a fallback dimension called **The Rift**.
- **The Rift (Fallback Dimension):** An emergency zone. You can escape it using an **Unstable Pocket**, or by finding the exit portal (Note: to use the Rift's physical exit portal, your inventory must be completely empty!).
- **Highly Configurable:** Customize room sizes, generation materials, and gameplay rules via the `pocket_dimension.toml` config file.
- **Admin Tools:** Built-in commands to locate players in their dimensions or reload the configuration on the fly.

---

## Crafting

![Crafting recipe](https://cdn.modrinth.com/data/cached_images/f2dc77ba791990f415df88e9f687c532e4026e7d.png)

---

Things to Know Before Playing

1. **Full Health Requirement:** You **must have full health** to enter your Pocket Dimension (can be disabled in config).
2. **Internal Breaking:** Breaking the Pocket block from *inside* gives you an **Unstable Pocket** and teleports you to **The Rift**.
3. **Escaping The Rift:** To use the Rift's physical exit, your **inventory must be completely empty**. Otherwise, use an Unstable Pocket to escape.
4. **Entity Relocation:** If the outside Pocket block is broken, any entities left inside are safely teleported back to the break location.

---

## Configuration

The mod is highly customizable. Upon first load, a `pocket_dimension.toml` file will be generated in your `config` folder. 

**Key Settings:**
- `require_full_health`: Set to `false` to allow entering the dimension while injured.
- `Room`: Customize the inner/outer sizes and wall thicknesses of newly generated rooms.
- `Blocks`: Change the default blocks used for the room's walls, floors, and light sources.

---

## Commands

- `/pocketdimension reload`: Reloads the configuration file without restarting the server. (Requires OP level 2)
- `/pocketdimension search <player>`: Locates exactly where a player is, whether they are in the overworld, in their pocket dimension, or near an exit. (Requires OP level 2)

---

## Credits

- Mod created by **Renwixx**
- Special thanks to **[MavLeague](https://modrinth.com/user/MavLeague)** for creating [original datapack](https://modrinth.com/mod/pocket_dimension) and some of the wonderful textures used in this mod.

## License

This project is licensed under the CC-BY-NC-4.0 License.
