package com.zos.common.ws.client.graphenej.models;

import com.zos.common.ws.client.graphenej.objects.AccountOptions;
import com.zos.common.ws.client.graphenej.objects.Authority;

public class LookupAsset {
    public String id;
    public String symbol;
    public long precision;
    public String issuer;
    public Object options;
    public String dynamic_asset_data_id;
    public long uasset_property;
    public Object[] whitelist_gateways;
}
