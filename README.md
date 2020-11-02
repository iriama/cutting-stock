Cutting Stock resolver coded in JAVA using [GLPK 4.65](https://www.gnu.org/software/glpk/).<br />
Download latest release [here](https://github.com/iriama/cutting-stock/releases).

# Problem

In operations research, the cutting-stock problem is the problem of cutting standard-sized pieces of stock material, such as paper rolls or sheet metal, into pieces of specified sizes while minimizing material wasted. It is an optimization problem in mathematics that arises from applications in industry. In terms of computational complexity, the problem is an NP-hard problem reducible to the knapsack problem. The problem can be formulated as an integer linear programming problem.
[Source](https://en.wikipedia.org/wiki/Cutting_stock_problem).

# Setup

1) Windows :
- Run `INSTALL_GLPK_JAVA.bat`

2) UNIX based (apt)
- Run `sudo apt install libglpk-java`

# Usage

`java -jar cutting_stock.jar [path to .txt problem instance]`

example :

`java -jar cutting_stock.jar problems/instance01.txt`

# Problem format

<pre>roll_width
order1_width order1_quantity
order2_width order2_quantity</pre>

example `problems/instance01.txt` :
<pre>100
45 97
36 610
31 395
14 211</pre>

output :
<pre>...
-------- Cutting solution
48.5 x (2x45.0cm)
100.75 x (2x36.0cm)
105.5 x (2x36.0cm + 2x14.0cm)
197.5 x (1x36.0cm + 2x31.0cm)
> pieces needed : 453 (ceil of calculated value : 452.25).
finished in 232 ms (I/O included).</pre>
