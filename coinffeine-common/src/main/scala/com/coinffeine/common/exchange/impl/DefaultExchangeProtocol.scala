package com.coinffeine.common.exchange.impl

import com.coinffeine.common.FiatCurrency
import com.coinffeine.common.bitcoin.{Address, ImmutableTransaction}
import com.coinffeine.common.exchange._

private[impl] class DefaultExchangeProtocol extends ExchangeProtocol {

  override def createHandshake[C <: FiatCurrency](
      exchange: Exchange[C],
      role: Role,
      unspentOutputs: Seq[UnspentOutput],
      changeAddress: Address): Handshake[C] = {
    val availableFunds = TransactionProcessor.valueOf(unspentOutputs.map(_.output))
    val depositAmount = role.myDepositAmount(exchange.amounts)
    require(availableFunds >= depositAmount,
      s"Expected deposit with $depositAmount ($availableFunds given)")
    val myDeposit = ImmutableTransaction {
      TransactionProcessor.createMultiSignedDeposit(
        unspentOutputs.map(_.toTuple), depositAmount, changeAddress,
        Seq(exchange.buyer.bitcoinKey, exchange.seller.bitcoinKey), exchange.parameters.network)
    }
    new DefaultHandshake(role, exchange, myDeposit)
  }
}

object DefaultExchangeProtocol {
  trait Component extends ExchangeProtocol.Component {
    override lazy val exchangeProtocol = new DefaultExchangeProtocol
  }
}
