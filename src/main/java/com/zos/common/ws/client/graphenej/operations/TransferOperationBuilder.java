package com.zos.common.ws.client.graphenej.operations;

import com.zos.common.ws.client.graphenej.errors.MalformedOperationException;
import com.zos.common.ws.client.graphenej.objects.AssetAmount;
import com.zos.common.ws.client.graphenej.objects.Memo;
import com.zos.common.ws.client.graphenej.objects.UserAccount;

/**
 * Factory class used to build a transfer operation
 */
public class TransferOperationBuilder extends BaseOperationBuilder {
    private UserAccount from;
    private UserAccount to;
    private AssetAmount transferAmount;
    private AssetAmount fee;
    private Memo memo;

    public TransferOperationBuilder setSource(UserAccount from) {
        this.from = from;
        return this;
    }

    public TransferOperationBuilder setDestination(UserAccount to) {
        this.to = to;
        return this;
    }

    public TransferOperationBuilder setTransferAmount(AssetAmount transferAmount) {
        this.transferAmount = transferAmount;
        return this;
    }

    public TransferOperationBuilder setFee(AssetAmount fee) {
        this.fee = fee;
        return this;
    }

    public TransferOperationBuilder setMemo(Memo memo) {
        this.memo = memo;
        return this;
    }

    @Override
    public TransferOperation build(){
        TransferOperation transferOperation;
        if(from == null ){
            throw new MalformedOperationException("Missing source account information");
        }else if(to == null){
            throw new MalformedOperationException("Missing destination account information");
        }else if(transferAmount == null){
            throw new MalformedOperationException("Missing transfer amount information");
        }
        if(fee != null){
            transferOperation = new TransferOperation(from, to, transferAmount, fee);
        }else{
            transferOperation = new TransferOperation(from, to, transferAmount);
        }
        if(memo != null){
            transferOperation.setMemo(this.memo);
        }
        return transferOperation;
    }
}
