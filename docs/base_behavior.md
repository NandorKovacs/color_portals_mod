# Place Block

No special behavior. Block is placed like a standard directional block.

# Base GUI

Has slot for color option, maybe some description of functionality, check to stay consistent with vanilla (regarding extent of explanation)

Insert dye → consumed →  Block tries to generate Portal with appropriate Color:

```none
blocks will be treaded as nodes, and each block has edges to all of its non diagonal neighbors. the base is considered to be the root, so that we have clear ranks
do a rank restricted breadth first search in the correct (depends on placement of the base) plane.
  if rank > ranklimit || current block != base or border block or air:
    consider attempt failed
    continue:
  if current block == air:
    place a portal block
    push neighbors on queue
    continue
  else:
    continue

if attempt failed:
  shatter all portal blocks
else:
  turn base into colored version
  calculate exact spawn position for this portal (lowest possible spawn under / on the base)

```

# Colored base GUI

Has cost indicator "cost: xxx". Has link button [link]

On open GUI:

```
if partner portal count > 0:
  calculate cost to the most recently activated one
  set cost indicator
else:
  turn button and cost indicator red
  on hover:
    "no portals to link to"
```

On button:

```
if not enough xp:
  on hover:
    "not enough xp"
  deaktivate button
else:
  unlink old portal connection of partner portal
  link portals
```

```
unlinking:
  set linked portal field to null for both portals
  turn special portal block into colored base
```

```
linking:
  set linked portal field to id of the partner portal
  turn base block into portal block with special blockstate
```

# In any active State, linked or not

```
def break_portal():
  shatter portal blocks
    (the special portal block will replace itself with an uncolored base instead of shattering, automatically)

if border block breaks:
  break_portal()

if portal_extinguished:
  break_portal()

if base broken:
  break_portal()
```