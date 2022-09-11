## Kali (Dungeon Editor)
With the editor players gets the ability to setup or update rooms for a specific floor in the dungeon.
That means a player can create different regions in a room and adjust some parameters of them.
The build process of a room is not possible with this extension. For that a team should use a normal Spigot or PaperMC server.



## Permissions
Some functionality requires permissions to use it.
The following list shows all current available permissions:

- editor.floor.delete

## Database

The execution of the editor requires a connection to a MongoDB server.
The extension itself uses the "Morphia" driver from MongoDB which allows us to use ORM.

In the Kali folder should be a database.json file with these following entries:

```json
{
    "host": "host",
    "port": 12900,
    "user": "user",
    "password": "password",
    "database": "database"
}
```

## Dependencies

Kali requires some dependencies which should be installed on a running Minestom server:
- Aves

These dependencies Kali includes automatically:
- Morphia
- Canis
- adventure-text-minimessage (should be checked if minestom includes that now)
