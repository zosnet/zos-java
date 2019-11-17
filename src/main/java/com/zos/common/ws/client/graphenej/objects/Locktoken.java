package com.zos.common.ws.client.graphenej.objects;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedLong;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.zos.common.ws.client.graphenej.Util;
import com.zos.common.ws.client.graphenej.Varint;
import com.zos.common.ws.client.graphenej.interfaces.ByteSerializable;
import com.zos.common.ws.client.graphenej.interfaces.JsonSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that represents a graphene locktoken.
 */
public class Locktoken extends GrapheneObject implements ByteSerializable, JsonSerializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Locktoken.class);
    public static final String PROXY_TO_SELF = "1.2.5";
    public static final String KEY_MEMBERSHIP_EXPIRATION_DATE = "membership_expiration_date";
    public static final String KEY_REGISTRAR = "registrar";
    public static final String KEY_REFERRER = "referrer";
    public static final String KEY_LIFETIME_REFERRER = "lifetime_referrer";
    public static final String KEY_NETWORK_FEE_PERCENTAGE = "network_fee_percentage";
    public static final String KEY_LIFETIME_REFERRER_FEE_PERCENTAGE = "lifetime_referrer_fee_percentage";
    public static final String KEY_REFERRER_REWARD_PERCENTAGE = "referrer_rewards_percentage";
    public static final String KEY_NAME = "name";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_OPTIONS = "options";
    public static final String KEY_STATISTICS = "statistics";
    public static final String KEY_WHITELISTING_ACCOUNTS = "whitelisting_accounts";
    public static final String KEY_BLACKLISTING_ACCOUNTS = "blacklisting_accounts";
    public static final String KEY_WHITELISTED_ACCOUNTS = "whitelisted_accounts";
    public static final String KEY_BLACKLISTED_ACCOUNTS = "blacklisted_accounts";
    public static final String KEY_OWNER_SPECIAL_AUTHORITY = "owner_special_authority";
    public static final String KEY_ACTIVE_SPECIAL_AUTHORITY = "active_special_authority";
    public static final String KEY_N_CONTROL_FLAGS = "top_n_control_flags";
    public static final String KEY_HASH64 = "hash64";
    public static final String KEY_STATE = "state";
    public static final String KEY_EXPIRATION = "expiration";

    @Expose
    private String issuer;

    @Expose
    private AssetAmount locked;

    @Expose
    private String to;

    @Expose
    private int period;

    @Expose
    private int type;

    @Expose
    private int autolock;

    @Expose
    private String create_time;

    @Expose
    private int rate;

    @Expose
    private int coinDay;

    @Expose
    private String lastCoinDayTime;

    @Expose
    private String remove_time;

    @Expose
    private String except_time;

    @Expose
    private AssetAmount interest;




    /**
     * Constructor that expects a user account in the string representation.
     * That is in the 1.2.x format.
     * @param id: The string representing the user account.
     */
    public Locktoken(String id) {
        super(id);
    }




    @Override
    public boolean equals(Object o) {
        return this.getObjectId().equals(((Locktoken)o).getObjectId());
    }

    @Override
    public int hashCode() {
        return this.getObjectId().hashCode();
    }
    
    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(byteArrayOutputStream);
        try {
            Varint.writeUnsignedVarLong(this.instance, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }




    @Override
    public String toJsonString() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }


        @Override
    public JsonObject toJsonObject() {
        JsonObject jsonAmount = new JsonObject();
        jsonAmount.addProperty("locktoken_id", id);
        return jsonAmount;
    }

    @Override
    public String toString() {
        return this.toJsonString();
    }


    /**
     * Deserializer used to build a UserAccount instance from the full JSON-formatted response obtained
     * by the 'get_objects' API call.
     */
    public static class UserAccountFullDeserializer implements JsonDeserializer<Locktoken> {

        @Override
        public Locktoken deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonAccount = json.getAsJsonObject();

            SimpleDateFormat dateFormat = new SimpleDateFormat(Util.TIME_DATE_FORMAT);

            // Retrieving and deserializing fields
            String id = jsonAccount.get(KEY_ID).getAsString();
//            String name = jsonAccount.get(KEY_NAME).getAsString();
//            Locktoken userAccount = new Locktoken(id, name);
//            AccountOptions options = context.deserialize(jsonAccount.get(KEY_OPTIONS), AccountOptions.class);
//            Authority owner = context.deserialize(jsonAccount.get(KEY_OWNER), Authority.class);
//            Authority active = context.deserialize(jsonAccount.get(KEY_ACTIVE), Authority.class);
//
//            // Setting deserialized fields into the created instance
//            userAccount.setRegistrar(jsonAccount.get(KEY_REGISTRAR).getAsString());
//
//            // Handling the deserialization and assignation of the membership date, which internally
//            // is stored as a long POSIX time value
//            try{
//                Date date = dateFormat.parse(jsonAccount.get(KEY_MEMBERSHIP_EXPIRATION_DATE).getAsString());
//                userAccount.setMembershipExpirationDate(date.getTime());
//            } catch (ParseException e) {
//                LOGGER.info("ParseException. Msg: "+e.getMessage());
//            }
//
//            // Setting the other fields
//            userAccount.setReferrer(jsonAccount.get(KEY_REFERRER).getAsString());
//            userAccount.setLifetimeReferrer(jsonAccount.get(KEY_LIFETIME_REFERRER).getAsString());
//            userAccount.setNetworkFeePercentage(jsonAccount.get(KEY_NETWORK_FEE_PERCENTAGE).getAsLong());
//            userAccount.setLifetimeReferrerFeePercentage(jsonAccount.get(KEY_LIFETIME_REFERRER_FEE_PERCENTAGE).getAsLong());
//            userAccount.setReferrerRewardsPercentage(jsonAccount.get(KEY_REFERRER_REWARD_PERCENTAGE).getAsLong());
//            userAccount.setOwner(owner);
//            userAccount.setActive(active);
//            userAccount.setOptions(options);
//            userAccount.setStatistics(jsonAccount.get(KEY_STATISTICS).getAsString());
//            return userAccount;
            return null;
        }
    }

    /**
     * Custom deserializer used to deserialize user accounts provided as response from the 'lookup_accounts' api call.
     * This response contains serialized user accounts in the form [[{id1},{name1}][{id1},{name1}]].
     *
     * For instance:
     *  [["bilthon-1","1.2.139205"],["bilthon-2","1.2.139207"],["bilthon-2016","1.2.139262"]]
     *
     * So this class will pick up this data and turn it into a UserAccount object.
     */
//    public static class UserAccountDeserializer implements JsonDeserializer<Locktoken> {
//
//        @Override
//        public Locktoken deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            JsonArray array = json.getAsJsonArray();
//            String name = array.get(0).getAsString();
//            String id = array.get(1).getAsString();
//            return new Locktoken(id, name);
//        }
//    }

    /**
     * Custom deserializer used to deserialize user accounts as provided by the response of the 'get_key_references' api call.
     * This response contains serialized user accounts in the form [["id1","id2"]]
     */
    public static class UserAccountSimpleDeserializer implements JsonDeserializer<Locktoken> {

        @Override
        public Locktoken deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String id = json.getAsString();
            return new Locktoken(id);
        }
    }

    public byte[] IntToByteArray(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }
}
