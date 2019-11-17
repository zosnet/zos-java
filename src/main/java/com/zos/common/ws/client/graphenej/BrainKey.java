package com.zos.common.ws.client.graphenej;

import com.zos.common.ws.client.graphenej.crypto.SecureRandomGenerator;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Class used to encapsulate all BrainKey-related operations.
 */
public class BrainKey {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrainKey.class);

    // The size of the word dictionary
    public static final int DICT_WORD_COUNT = 49744;

    /* The required number of words */
    public static final int BRAINKEY_WORD_COUNT = 12;

    /* The default sequence number is zero */
    public static final int DEFAULT_SEQUENCE_NUMBER = 0;

    /* The corresponding private key derivated from the brain key */
    private ECKey mPrivateKey;

    /* The actual words from this brain key + the sequence number */
    private String mBrainKey;

    /* The sequence number */
    private int sequenceNumber;

    /**
     * Method that will generate a random brain key
     *
     * @param words The list of words from the graphene specification
     * dictionary.
     * @return A random sequence of words
     */
    public static String suggest(String words) {
        String[] wordArray = words.split(",");
        ArrayList<String> suggestedBrainKey = new ArrayList<String>();
        assert (wordArray.length == DICT_WORD_COUNT);
        SecureRandom secureRandom = SecureRandomGenerator.getSecureRandom();
        int index;
        for (int i = 0; i < BRAINKEY_WORD_COUNT; i++) {
            index = secureRandom.nextInt(DICT_WORD_COUNT - 1);
            suggestedBrainKey.add(wordArray[index].toUpperCase());
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(String word : suggestedBrainKey){
            stringBuilder.append(word);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString().trim();
    }
    /**
     * BrainKey constructor that takes as argument a specific brain key word
     * sequence and generates the private key and address from that.
     *
     * @param words The brain key specifying the private key
     * @param sequence Sequence number
     */
    public BrainKey(String words, int sequence) {
        this.mBrainKey = words;
        this.sequenceNumber = sequence;
        String encoded = String.format("%s %d", words, sequence);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(encoded.getBytes("UTF-8"));
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] result = sha256.digest(bytes);
            mPrivateKey = ECKey.fromPrivate(result);

        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("NoSuchAlgotithmException. Msg: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("UnsupportedEncodingException. Msg: " + e.getMessage());
        }
    }

    /**
     * Gets the array of bytes representing the public key.
     * @return
     */
    public byte[] getPublicKey() {
        return mPrivateKey.getPubKey();
    }

    /**
     * Returns the private key as an instance of the ECKey class.
     * @return
     */
    public ECKey getPrivateKey() {
        return mPrivateKey;
    }

    /**
     * Returns the private key in the Wallet Import Format for the uncompressed private key.
     * @see <a href="https://en.bitcoin.it/wiki/Wallet_import_format">WIF</a>
     * @return
     */
    public String getWalletImportFormat(){
        DumpedPrivateKey wif = this.mPrivateKey.decompress().getPrivateKeyEncoded(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));
        return wif.toString();
    }

    /**
     * Returns the public address derived from this brain key
     * @param prefix: The prefix to use in this address.
     * @return An instance of the {@link Address} class
     */
    public Address getPublicAddress(String prefix){
        return new Address(ECKey.fromPublicOnly(getPublicKey()), prefix);
    }

    /**
     * Brain key words getter
     * @return: The word sequence that comprises this brain key
     */
    public String getBrainKey(){
        return mBrainKey;
    }

    /**
     * Sequence number getter
     * @return: The sequence number used alongside with the brain key words in order
     * to derive the private key
     */
    public int getSequenceNumber(){
        return sequenceNumber;
    }
}
