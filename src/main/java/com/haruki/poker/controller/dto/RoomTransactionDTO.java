package com.haruki.poker.controller.dto;

import java.util.List;
import lombok.Data;

@Data
public class RoomTransactionDTO {
    private UserDetailDTO userDetail;
    private List<UserDetailDTO> allUserDetails;
    private List<TransactionRecordDTO> transactionRecords;
} 