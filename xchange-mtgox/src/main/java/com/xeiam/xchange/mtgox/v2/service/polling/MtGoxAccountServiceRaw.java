package com.xeiam.xchange.mtgox.v2.service.polling;

import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.mtgox.MtGoxUtils;
import com.xeiam.xchange.mtgox.v2.MtGoxV2;
import com.xeiam.xchange.mtgox.v2.dto.MtGoxException;
import com.xeiam.xchange.mtgox.v2.dto.account.polling.*;
import com.xeiam.xchange.mtgox.v2.service.MtGoxV2Digest;
import com.xeiam.xchange.service.polling.BasePollingExchangeService;
import com.xeiam.xchange.utils.Assert;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author gnandiga
 */
public class MtGoxAccountServiceRaw extends BasePollingExchangeService {

    protected final MtGoxV2 mtGoxV2;
    protected final MtGoxV2Digest signatureCreator;

    /**
     * Initialize common properties from the exchange specification
     *
     * @param exchangeSpecification The {@link com.xeiam.xchange.ExchangeSpecification}
     */
    protected MtGoxAccountServiceRaw(ExchangeSpecification exchangeSpecification) {
        super(exchangeSpecification);

        Assert.notNull(exchangeSpecification.getSslUri(), "Exchange specification URI cannot be null");
        this.mtGoxV2 = RestProxyFactory.createProxy(MtGoxV2.class, exchangeSpecification.getSslUri());
        this.signatureCreator = MtGoxV2Digest.createInstance(exchangeSpecification.getSecretKey());
    }

    public MtGoxAccountInfo getMtGoxAccountInfo() throws IOException {

        try {
            MtGoxAccountInfoWrapper mtGoxAccountInfoWrapper = mtGoxV2.getAccountInfo(exchangeSpecification.getApiKey(), signatureCreator, MtGoxUtils.getNonce());
            if (mtGoxAccountInfoWrapper.getResult().equals("success")) {
                return mtGoxAccountInfoWrapper.getMtGoxAccountInfo();
            }
            else if (mtGoxAccountInfoWrapper.getResult().equals("error")) {
                throw new ExchangeException("Error calling getAccountInfo(): " + mtGoxAccountInfoWrapper.getError());
            }
            else {
                throw new ExchangeException("Error calling getAccountInfo(): Unexpected result!");
            }
        } catch (MtGoxException e) {
            throw new ExchangeException("Error calling getAccountInfo(): " + e.getError(), e);
        }
    }

    public MtGoxWithdrawalResponse mtGoxWithdrawFunds(BigDecimal amount, String address) throws IOException {
        try {
          MtGoxWithdrawalResponseWrapper mtGoxWithdrawalResponseWrapper =
              mtGoxV2.withdrawBtc(exchangeSpecification.getApiKey(), signatureCreator, MtGoxUtils.getNonce(), address, amount.multiply(
                  new BigDecimal(MtGoxUtils.BTC_VOLUME_AND_AMOUNT_INT_2_DECIMAL_FACTOR)).intValue(), 1, false, false);

          if (mtGoxWithdrawalResponseWrapper.getResult().equals("success")) {
            return mtGoxWithdrawalResponseWrapper.getMtGoxWithdrawalResponse();
          }
          else if (mtGoxWithdrawalResponseWrapper.getResult().equals("error")) {
            throw new ExchangeException("Error calling withdrawFunds(): " + mtGoxWithdrawalResponseWrapper.getError());
          }
          else {
            throw new ExchangeException("Error calling withdrawFunds(): Unexpected result!");
          }
        } catch (MtGoxException e) {
          throw new ExchangeException("Error calling withdrawFunds(): " + e.getError(), e);
        }
    }

    public MtGoxBitcoinDepositAddress mtGoxRequestDepositAddress(String description, String notificationUrl) throws IOException {
        try {
          MtGoxBitcoinDepositAddressWrapper mtGoxBitcoinDepositAddressWrapper =
              mtGoxV2.requestDepositAddress(exchangeSpecification.getApiKey(), signatureCreator, MtGoxUtils.getNonce(), description, notificationUrl);
          if (mtGoxBitcoinDepositAddressWrapper.getResult().equals("success")) {
            return mtGoxBitcoinDepositAddressWrapper.getMtGoxBitcoinDepositAddress();
          }
          else if (mtGoxBitcoinDepositAddressWrapper.getResult().equals("error")) {
            throw new ExchangeException("Error calling requestBitcoinDepositAddress(): " + mtGoxBitcoinDepositAddressWrapper.getError());
          }
          else {
            throw new ExchangeException("Error calling requestBitcoinDepositAddress(): Unexpected result!");
          }
        } catch (MtGoxException e) {
          throw new ExchangeException("Error calling requestBitcoinDepositAddress(): " + e.getError(), e);
        }
    }
}
