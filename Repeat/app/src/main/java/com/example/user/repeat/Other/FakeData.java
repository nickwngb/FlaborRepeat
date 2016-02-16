package com.example.user.repeat.Other;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hao_jun on 2016/2/16.
 */
public class FakeData {


    public static List<PARecord> getPARecord() {
        List<PARecord> palist = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PARecord par = new PARecord();
            par.tag = PARecord.TAG_PRecord;
            par.setProblemStatus(Code.Untreated);
            par.setPRSNo(i + 1);
            par.setResponseContent("Content" + i);
            par.setResponseDate("10/1" + i);
            par.setResponseID("ID" + i);
            if (i % 3 == 0) {
                par.setResponseRole(Code.Manager);
            } else {
                par.setResponseRole(Code.Flabor);
            }
            palist.add(par);
        }

        return palist;
    }

    public static List<ProblemResponse> getResponse(int PRSNo) {
        List<ProblemResponse> result = new ArrayList<>();
        for (int i = 0; i < PRSNo; i++) {
            ProblemResponse pr = new ProblemResponse();
            pr.setPRSNo(PRSNo);
            pr.setResponseContent(i % 3 == 1 ? "OK" : "Hello");
            pr.setResponseID("ID" + i);
            pr.setResponseDate("10/1" + i);
            pr.setResponseRole(i % 3 == 1 ? Code.Manager : Code.Flabor);
            result.add(pr);
        }

        return result;
    }
}
