package com.codecool.kgp.auth;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LdtToDateAdapter extends Date {

    public LdtToDateAdapter(LocalDateTime ldt) {
        super(ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }
}
