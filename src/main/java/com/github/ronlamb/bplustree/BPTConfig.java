package com.github.ronlamb.bplustree;

public class BPTConfig {
    int branchFactor;   // Max direct child nodes
    double density;		// How dense to keep the screen as a percentage
                        // from 50 to 85% default is 50
    int maxBranchRefactor;
    int minKeys;
    int maxKeys;
    int minLeaves;
    int maxLeaves;
    boolean rebalance;

    public BPTConfig(int branchFactor, double density) {
        this.branchFactor = branchFactor;
        this.density = density;
        this.maxKeys = branchFactor;
        this.minKeys = (int) Math.ceil(maxKeys/2.0);
        this.maxLeaves =branchFactor;
        this.minLeaves = (int) Math.ceil(maxLeaves /2.0);

        calcBranchResize();
    }

    private void calcBranchResize() {
        maxBranchRefactor = (int) Math.round(branchFactor * 1.0 * density / 100.0);
        if (minLeaves == maxBranchRefactor) {
            rebalance = false;
        } else {
            rebalance = true;
        }
    }
}
