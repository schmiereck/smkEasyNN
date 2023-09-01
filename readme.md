


Ident:
* values between 0.0 an 1.0
* i(r,g,b) sum is always 1.0
* global value of all cells of a individuum

State:
* values between 0.0 an 1.0
* s(r,g,b) values between 0.0 an 1.0
* i(r,g,b) sum of values is always 1.0

Target:
* State == Ident

Input:
* me: i0, s0
* neighbours: i1, s1, i2, s2, i3, s3,

Output:
* transfer +/- x % of differenz between s0 and s1, s2, s3
* transfer +/- y % of differenz between s0 and i1, i2, i3
  * controlled: if i of neighbour cell is (nearly) equal to net ident then cell is part of the net

Architecture:
* controlled cells
  * every controlled cell has a net instance 
    for itself and the neighboring cells
* meta level of controlled neighbour cells
  * meta-net see and affects the average of controlled cells
* Node: Netz für Zelle mit Nachbarn
* Meta-Node: 

```
|-|-|-| |i| |-|-|-|
|-|-|-|  I  |-|-|-|   m
|-|-|  I  |-|-|-|-|   m
|-|-|-|-|  I  |-|-|   m
|-|-|    I    |-|-|   mm

           i0
          /  \
         i1   i2
        /  \ /  \
       i         i
```
