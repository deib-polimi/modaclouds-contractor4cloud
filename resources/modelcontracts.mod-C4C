set CONTRACT;
set TIME_INT;

param PercentageOfS >= 0, <= 1;
param CostD >= 0;
param CostS{TIME_INT} >= 0;
param CostR{CONTRACT} >= 0;
param InitialCostR{CONTRACT} >= 0;
param Instances{TIME_INT} >= 0;
param M > 1;
param DaysConsidered >= 1;

var D{TIME_INT} >= 0 integer;
var S{TIME_INT} >= 0 integer;
var R{CONTRACT, TIME_INT} >= 0 integer;
var X{CONTRACT} >= 0 integer;
var Y{CONTRACT} binary;

minimize Total_Cost:
    sum{c in CONTRACT} (InitialCostR[c] * X[c]) / DaysConsidered + sum{t in TIME_INT} (CostD * D[t] + CostS[t] * S[t] + sum{c in CONTRACT} (CostR[c] * R[c, t]) );

subject to AllInstancesAllocated{t in TIME_INT}: D[t] + S[t] + sum{c in CONTRACT} (R[c, t]) = Instances[t];
subject to PercentageOfOnSpotRespected{t in TIME_INT}: S[t] <= (PercentageOfS / (1 - PercentageOfS)) * (D[t] + sum{c in CONTRACT} (R[c, t]));
subject to C13{c in CONTRACT}: X[c] <= M * Y[c];
subject to C14{c in CONTRACT, t in TIME_INT}: R[c, t] <= M * Y[c];
subject to C15{c in CONTRACT, t in TIME_INT}: R[c, t] <= X[c];
subject to OnlyOneTypeOfContract: sum{c in CONTRACT} (Y[c]) = 1;
