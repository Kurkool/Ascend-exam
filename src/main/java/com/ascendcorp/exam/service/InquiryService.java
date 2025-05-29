package com.ascendcorp.exam.service;

import com.ascendcorp.exam.model.BankApiCode;
import com.ascendcorp.exam.model.InquiryServiceResultDTO;
import com.ascendcorp.exam.model.TransferResponse;
import com.ascendcorp.exam.proxy.BankProxyGateway;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

public class InquiryService {

    @Autowired
    private BankProxyGateway bankProxyGateway;

    private static final Logger log = Logger.getLogger(InquiryService.class);

    private static final String CODE_SUCCESS = "200";
    private static final String CODE_BAD_REQUEST = "400";
    private static final String CODE_INTERNAL_SERVER_ERROR = "500";
    private static final String CODE_UNKNOWN_BANK_RESPONSE = "501";
    private static final String CODE_SERVICE_TIMEOUT = "503";
    private static final String CODE_GATEWAY_ERROR = "504";
    private static final String CODE_98_ERROR = "98";

    private static final String DESC_GENERAL_INVALID_DATA = "General Invalid Data";
    private static final String DESC_GENERAL_TRANSACTION_ERROR = "General Transaction Error";
    private static final String DESC_INTERNAL_APPLICATION_ERROR = "Internal Application Error";
    private static final String DESC_ERROR_TIMEOUT = "Error timeout";

    public InquiryServiceResultDTO inquiry(String transactionId,
                                           Date tranDateTime,
                                           String channel,
                                           String locationCode,
                                           String bankCode,
                                           String bankNumber,
                                           double amount,
                                           String reference1,
                                           String reference2,
                                           String firstName,
                                           String lastName) {
        InquiryServiceResultDTO respDTO = new InquiryServiceResultDTO();
        try {
            log.info("validate request parameters.");
            validateRequestParameters(transactionId, tranDateTime, channel, bankCode, bankNumber, amount);

            log.info("Calling bank web service...");
            TransferResponse response = bankProxyGateway.requestTransfer(transactionId, tranDateTime, channel,
                    bankCode, bankNumber, amount, reference1, reference2);

            if (Objects.nonNull(response)) {
                log.debug("Processing bank response...");
                processBankResponse(response, respDTO);
            } else {
                throw new Exception("Unable to inquiry from service.");
            }
        } catch (NullPointerException ne) {
            handleNullPointerException(respDTO);
        } catch (WebServerException r) {
            handleWebServerException(respDTO, r);
        } catch (Exception e) {
            log.error("Inquiry exception", e);
            handleGeneralException(respDTO);
        }
        return respDTO;
    }

    private void validateRequestParameters(String transactionId, Date tranDateTime, String channel,
                                           String bankCode, String bankNumber, double amount) {
        if (!StringUtils.hasText(transactionId)) throw new NullPointerException("Transaction id is required!");
        if (tranDateTime == null) throw new NullPointerException("Transaction DateTime is required!");
        if (!StringUtils.hasText(channel)) throw new NullPointerException("Channel is required!");
        if (!StringUtils.hasText(bankCode)) throw new NullPointerException("Bank Code is required!");
        if (!StringUtils.hasText(bankNumber)) throw new NullPointerException("Bank Number is required!");
        if (amount <= 0) throw new NullPointerException("Amount must be more than zero!");
    }

    private void processBankResponse(TransferResponse response, InquiryServiceResultDTO respDTO) {
        respDTO.setRefNo1(response.getReferenceCode1());
        respDTO.setRefNo2(response.getReferenceCode2());
        respDTO.setAmount(response.getAmount());
        respDTO.setTranID(response.getBankTransactionID());

        switch (BankApiCode.fromString(response.getResponseCode())) {
            case APPROVED:
                respDTO.setReasonCode(CODE_SUCCESS);
                respDTO.setReasonDesc(response.getDescription());
                respDTO.setAccountName(response.getDescription());
                break;
            case INVALID_DATA:
                processInvalidDataResponse(response, respDTO);
                break;
            case TRANSACTION_ERROR:
                processTransactionErrorResponse(response, respDTO);
                break;
            case UNKNOWN:
                processUnknownResponse(response, respDTO);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Error Reason Code");
        }
    }

    private void processInvalidDataResponse(TransferResponse response, InquiryServiceResultDTO respDTO) {
        String replyDesc = response.getDescription();
        if (StringUtils.hasText(replyDesc)) {
            String[] respDesc = replyDesc.split(":");
            if (respDesc.length >= 3) {
                respDTO.setReasonCode(respDesc[1]);
                respDTO.setReasonDesc(respDesc[2]);
            } else {
                respDTO.setReasonCode(CODE_BAD_REQUEST);
                respDTO.setReasonDesc(DESC_GENERAL_INVALID_DATA);
            }
        } else {
            respDTO.setReasonCode(CODE_BAD_REQUEST);
            respDTO.setReasonDesc(DESC_GENERAL_INVALID_DATA);
        }
    }

    private void processTransactionErrorResponse(TransferResponse response, InquiryServiceResultDTO respDTO) {
        String replyDesc = response.getDescription();
        if (StringUtils.hasText(replyDesc)) {
            String[] respDesc = replyDesc.split(":");
            if (respDesc.length >= 2) {
                String subIdx1 = respDesc[0];
                String subIdx2 = respDesc[1];
                log.info("Case Inquiry Error Code Format: index[0] = " + subIdx1 + ", index[1] = " + subIdx2);
                if (CODE_98_ERROR.equalsIgnoreCase(subIdx1)) {
                    respDTO.setReasonCode(subIdx1);
                    respDTO.setReasonDesc(subIdx2);
                } else {
                    log.info("case error is not 98 code");
                    if (respDesc.length >= 3) {
                        String subIdx3 = respDesc[2];
                        log.info("index[2] = " + subIdx3);
                        respDTO.setReasonCode(subIdx2);
                        respDTO.setReasonDesc(subIdx3);
                    } else {
                        respDTO.setReasonCode(subIdx1);
                        respDTO.setReasonDesc(subIdx2);
                    }
                }
            } else {
                respDTO.setReasonCode(CODE_INTERNAL_SERVER_ERROR);
                respDTO.setReasonDesc(DESC_GENERAL_TRANSACTION_ERROR);
            }
        } else {
            respDTO.setReasonCode(CODE_INTERNAL_SERVER_ERROR);
            respDTO.setReasonDesc(DESC_GENERAL_TRANSACTION_ERROR);
        }
    }

    private void processUnknownResponse(TransferResponse response, InquiryServiceResultDTO respDTO) {
        String replyDesc = response.getDescription();
        if (StringUtils.hasText(replyDesc)) {
            String[] respDesc = replyDesc.split(":");
            if (respDesc.length >= 2) {
                respDTO.setReasonCode(respDesc[0]);
                respDTO.setReasonDesc(StringUtils.hasText(respDesc[1]) && !respDesc[1].trim().isEmpty() ? respDesc[1] : DESC_GENERAL_INVALID_DATA);
            } else {
                respDTO.setReasonCode(CODE_UNKNOWN_BANK_RESPONSE);
                respDTO.setReasonDesc(DESC_GENERAL_INVALID_DATA);
            }
        } else {
            respDTO.setReasonCode(CODE_UNKNOWN_BANK_RESPONSE);
            respDTO.setReasonDesc(DESC_GENERAL_INVALID_DATA);
        }
    }

    private void handleNullPointerException(InquiryServiceResultDTO respDTO) {
        respDTO.setReasonCode(CODE_INTERNAL_SERVER_ERROR);
        respDTO.setReasonDesc(DESC_GENERAL_INVALID_DATA);
    }

    private void handleWebServerException(InquiryServiceResultDTO respDTO, WebServerException r) {
        String faultString = r.getMessage();
        if (StringUtils.hasText(faultString) && (faultString.contains("java.net.SocketTimeoutException")
                || faultString.contains("Connection timed out"))) {
            respDTO.setReasonCode(CODE_SERVICE_TIMEOUT);
            respDTO.setReasonDesc(DESC_ERROR_TIMEOUT);
        } else {
            respDTO.setReasonCode(CODE_GATEWAY_ERROR);
            respDTO.setReasonDesc(DESC_INTERNAL_APPLICATION_ERROR);
        }
    }

    private void handleGeneralException(InquiryServiceResultDTO respDTO) {
        respDTO.setReasonCode(CODE_GATEWAY_ERROR);
        respDTO.setReasonDesc(DESC_INTERNAL_APPLICATION_ERROR);
    }
}