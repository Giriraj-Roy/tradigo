package com.roy.tradigo.model;

import com.roy.tradigo.domain.VerificationType;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean is_enabled=false;
    private VerificationType sendTo;
}
