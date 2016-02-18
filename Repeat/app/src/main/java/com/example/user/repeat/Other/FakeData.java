package com.example.user.repeat.Other;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hao_jun on 2016/2/16.
 */
public class FakeData {
    public static List<PARecord> getPARecord() {
        List<PARecord> palist = new ArrayList<>();
        PARecord par = new PARecord();
        par.tag = PARecord.TAG_Problem;
        par.setProblemStatus(Code.Untreated);
        par.setPRSNo(2);
        par.setResponseContent("I want eat some food");
        par.setResponseDate("2/17");
        par.setResponseID("John");
        par.setResponseRole(Code.Flabor);
        palist.add(par);

        PARecord par2 = new PARecord();
        par2.tag = PARecord.TAG_Announcement;
        par2.setPushContent("Good Morning Every");
        par2.setCreateDate("2/17");
        par2.setCreateID("admin");
        palist.add(par2);

        return palist;
    }

    public static List<ProblemResponse> getResponse(int PRSNo) {
        List<ProblemResponse> result = new ArrayList<>();
        for (int i = 0; i < PRSNo; i++) {
            ProblemResponse pr = new ProblemResponse();
            pr.setPRSNo(PRSNo);
            pr.setResponseContent(i % 2 == 0 ? "I want eat some food" : "OK");
            pr.setResponseID(i % 2 == 0 ? "John" : "admin");
            pr.setResponseDate("2/17");
            pr.setResponseRole(i % 2 == 0 ? Code.Flabor : Code.Manager);
            result.add(pr);
        }
        return result;
    }
}
