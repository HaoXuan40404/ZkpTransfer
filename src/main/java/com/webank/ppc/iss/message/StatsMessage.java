package com.webank.ppc.iss.message;

import lombok.Data;

/** @author caryliao */
@Data
public class StatsMessage {
    long agencyCount;
    long algorithmCount;
    long myAlgorithmCount;
    long datasetCount;
    long myDatasetCount;
}
