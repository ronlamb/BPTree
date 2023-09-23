package com.github.ronlamb.bplustree;

public class BPTConfig {
    private static final double minDensity = 50.0;
    private static final double maxDensity = 90.0;

    int branchFactor;   // Max records and keys.  Child ptrs = 1 + branchFactor
    double density;		// How dense to keep the screen as a percentage
                        // from 50 to 85% default is 50
    int maxBranchRefactor;
    int maxKeys;
    int midKeys;
    int maxLeaveRecords;
    int midLeaveRecords;
    boolean rebalance;
    boolean allowDuplicates;

    int size;

    public BPTConfig(int branchFactor, double density) {
        if (density < minDensity) {
            density = minDensity;
        }

        if (density > maxDensity) {
            density = maxDensity;
        }

        this.branchFactor = branchFactor;
        this.density = density;
        this.maxKeys = branchFactor;
        this.midKeys = (int) Math.ceil((maxKeys +1)/ 2.0);
        this.maxLeaveRecords =branchFactor;
        this.midLeaveRecords = (int) Math.ceil((maxLeaveRecords + 1)/ 2.0) ;

        allowDuplicates = false;    // For now never allow duplicates
        calcBranchResize();
    }

    private void calcBranchResize() {
        maxBranchRefactor = (int) Math.round(branchFactor * 1.0 * density / 100.0);
        rebalance = midLeaveRecords != maxBranchRefactor;
    }
}
