# BPTree
B+Tree Java in memory implementation

Simple implementation of a B+Tree in Java.

## Limitations

The BPTree class does not check for duplicate keys.

This may be added later as an enhancement.

## Timings

The following timings were done useing Visual VM running Main.
Timings done on:
* Windows 10
* Ryzen 5900x
* 64 GB Ram


| Timings                           | Calls  | Time (ms) | Time / insert (ms) | Reduction |
|-----------------------------------|--------|-----------|--------------------|-----------|
| Before optimizations              | 252590 | 2240      | 0.00868            | Base      |
| BPTree.findPrevKey binary search  | 244178 | 2023      | 0.008285           | 6.6%      |
| BPTreeleafIndex binary search     | 247415 | 1808      | 0.007305           | 17.6%     |
| InternalNode.insert binary search | 258700 | 1759      | 0.006902           | 22.2%     |
| parentIndex for quicker leaf index | 249496 | 1651 | 0.006617 | 23.8% |
### TODO:

1. Update record value to new value if key is found
2. Look into replacing Binary Tree search with internally  

