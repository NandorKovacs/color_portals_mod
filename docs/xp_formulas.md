this will be subject to change later on. for now we agreed on the following terms:

# Overall formula for portal a and portal b:

The results are in minecraft levels, not experience points

`x = horizontal, euclidean distance between the base of a and b`

`base_cost + dim_cost_a + dim_cost_b + max(dist_cost_a(x), dist_cost_b(x))`

# Dim costs

#### Overworld: 	

`dim_cost = 0`
`dist_cost(x) = normal_dist_cost(x)`

#### Nether:

`dim_cost = 20`
`dist_cost(x) = normal_dist_cost(x * 8)`

#### End: 	

`dim_cost = 50 + dist_cost(1024)`

`dist_cost(x) = normal_dist_cost(max(0, x - 1024))`

# Default costs:

`normal_dist_cost(x) = (x/160)^2`
`base_cost = 5`