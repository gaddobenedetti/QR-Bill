/*
 * Copyright 2018 Gaddo F Benedetti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gfb.test;

import java.util.ArrayList;

/**
 * <h1>Swiss Payments Code Serializer</h1>
 *
 * <p>This class was designed to be a bare bones serializer for the Swiss Payments Code, which is
 * meant to adapt the payment system in Switzerland and Liechtenstein to the international ISO 20022
 * standard. For more information on this please consult the
 * <a href="http://www.paymentstandards.ch/">PaymentStandards.ch</a> site, as the
 * <a href="https://www.paymentstandards.ch/dam/downloads/ig-qr-bill-en.pdf">implementation
 * guide</a> here was the basis for this class.</p>
 *
 * <p>This class was purposely written using only core Java and without any imports, so that it can
 * more easily adapted to other languages and platforms. As such it does not implement
 * {@link java.io.Serializable}, out of the box, but will do so if simply added. It is designed, not
 * only to serialize, but also to validate data according to the above standard.</p>
 *
 * @author  Gaddo F Benedetti
 * @version 2.3.1
 * @since   2019-11-23
 */

public class QRBill {

    // TODO Remove the use of the strict param, as it serves little practical purpose.
    private String qrType;
    private Float version;
    private int codingType;
    private String account;
    private float amount;
    private String currency;
    private String dueDate;
    private String referenceType;
    private String reference;
    private String unstructuredMsg = "";
    private String trailer;
    private String billInfo = "";
    private String[] as = new String[] { "", "" };
    private Actor[] actors = new Actor[] {
            new Actor(ACTOR_CR),
            new Actor(ACTOR_UCR),
            new Actor(ACTOR_UDR)
    };

    /**
     * QR Type Identifier: Swiss Payments Code
     */
    public static final String      QRTYPE_SPC          = "SPC";
    /**
     * Character Set: Latin (ISO-8859-1)
     */
    public static final int         CODING_LATIN_1      = 1;
    /**
     * Actor Type: Creditor
     */
    public static final int         ACTOR_CR            = 0;
    /**
     * Actor Type: Ultimate creditor
     */
    public static final int         ACTOR_UCR           = 1;
    /**
     * Actor Type: Ultimate debtor
     */
    public static final int         ACTOR_UDR           = 2;
    /**
     * Address Type: Structured
     */
    public static final String      ADDTYPE_STRUCTURED  = "S";
    /**
     * Address Type: Combined
     */
    public static final String      ADDTYPE_COMBINED    = "K";
    /**
     * Currency: CHF - Swiss Francs
     */
    public static final String      CURRENCY_CHF       = "CHF";
    /**
     * Currency: EUR - Euro
     */
    public static final String      CURRENCY_EUR        = "EUR";
    /**
     * Reference Type: QR Reference
     */
    public static final String      REFTYPE_QRR         = "QRR";
    /**
     * Reference Type: Creditor Reference (ISO 11649)
     */
    public static final String      REFTYPE_SCOR        = "SCOR";
    /**
     * Reference Type: None
     */
    public static final String      REFTYPE_NON         = "NON";
    /**
     * Trailer: End Payment Data (EPD)
     */
    public static final String      TRAILER_EPD         = "EPD";


    private static final float      VERSION_SUPPORTED   = 2.00F;


    private static enum Data {
        NONE, QRTYPE, VERSION, CODING, ACCOUNT, AMOUNT, CURRENCY, DUEDATE, REF_TYPE, REF,
        CR_ADDTYPE, CR_NAME, CR_ADDRESS1, CR_ADDRESS2, CR_POSTCODE, CR_LOCATION, CR_COUNTRY,
        UCR_ADDTYPE, UCR_NAME, UCR_ADDRESS1, UCR_ADDRESS2, UCR_POSTCODE, UCR_LOCATION, UCR_COUNTRY,
        UDR_ADDTYPE, UDR_NAME, UDR_ADDRESS1, UDR_ADDRESS2, UDR_POSTCODE, UDR_LOCATION, UDR_COUNTRY,
        UNSTR_MSG, TRAILER, BILLINFO, ALTSCHEMA1, ALTSCHEMA2
    }

    private final Data[] version1 = {
            Data.QRTYPE, Data.VERSION, Data.CODING, Data.ACCOUNT,
            Data.CR_NAME, Data.CR_ADDRESS1, Data.CR_ADDRESS2, Data.CR_POSTCODE, Data.CR_LOCATION, Data.CR_COUNTRY,
            Data.UCR_NAME, Data.UCR_ADDRESS1, Data.UCR_ADDRESS2, Data.UCR_POSTCODE, Data.UCR_LOCATION, Data.UCR_COUNTRY,
            Data.AMOUNT, Data.CURRENCY, Data.DUEDATE,
            Data.UDR_NAME, Data.UDR_ADDRESS1, Data.UDR_ADDRESS2, Data.UDR_POSTCODE, Data.UDR_LOCATION, Data.UDR_COUNTRY,
            Data.REF_TYPE, Data.REF, Data.UNSTR_MSG, Data.ALTSCHEMA1, Data.ALTSCHEMA2
    };

    private final Data[] version2 = {
            Data.QRTYPE, Data.VERSION, Data.CODING, Data.ACCOUNT,
            Data.CR_ADDTYPE, Data.CR_NAME, Data.CR_ADDRESS1, Data.CR_ADDRESS2, Data.CR_POSTCODE, Data.CR_LOCATION, Data.CR_COUNTRY,
            Data.UCR_ADDTYPE, Data.UCR_NAME, Data.UCR_ADDRESS1, Data.UCR_ADDRESS2, Data.UCR_POSTCODE, Data.UCR_LOCATION, Data.UCR_COUNTRY,
            Data.AMOUNT, Data.CURRENCY,
            Data.UDR_ADDTYPE, Data.UDR_NAME, Data.UDR_ADDRESS1, Data.UDR_ADDRESS2, Data.UDR_POSTCODE, Data.UDR_LOCATION, Data.UDR_COUNTRY,
            Data.REF_TYPE, Data.REF, Data.UNSTR_MSG, Data.TRAILER, Data.BILLINFO, Data.ALTSCHEMA1, Data.ALTSCHEMA2
    };

    /**
     * Constructor that generates a partially empty QR Billing object, with a number of default
     * values. These values are:
     * <ul>
     *     <li>QR Type Identifier: {@link #QRTYPE_SPC}</li>
     *     <li>Version: {@value #VERSION_SUPPORTED}</li>
     *     <li>Character Set: {@link #CODING_LATIN_1}</li>
     *     <li>Reference Type: {@link #REFTYPE_NON}</li>
     *     <li>Amount: None</li>
     *     <li>Currency: {@link #CURRENCY_CHF}</li>
     *     <li>Trailer: {@link #TRAILER_EPD}</li>
     * </ul>
     *
     * Please note that this constructor and the use of the strict paramater is deprecated and
     * will be removed in the future.
     */
    @Deprecated
    public QRBill() {
        setQrType(QRBill.QRTYPE_SPC);
        setVersion(QRBill.VERSION_SUPPORTED);
        setCodingType(CODING_LATIN_1);
        setReference();
        setAmount();
        setCurrency(QRBill.CURRENCY_CHF);
        setTrailer(QRBill.TRAILER_EPD);
    }

    /**
     * Constructor that generates a QR Billing object, using raw QR Bill data as input.
     *
     * @param rawData String. Basic QR Bill data. Fields should be separated by new lines. An
     *                implementation guide on the format may be found at
     *                http://www.paymentstandards.ch/
     */
    public QRBill(String rawData) {
        validateData(rawData);
    }

    /**
     * Returns whether the current QR Bill is valid.
     *
     * @return Boolean. Whether the current QR Bill is valid or not.
     */
    public boolean isValid () {
        return validateData(toString()).size() == 0;
    }

    /**
     * Returns the Unvalidated QR Code in the QR Billing object.
     *
     * @return String. The unvalidated QR Bill data. Fields will be separated by new lines. An
     * implementation guide on the format may be found at http://www.paymentstandards.ch/
     */
    @Override
    public String toString () {

        Data[] structure = getStructure();
        StringBuffer out = new StringBuffer();
        if (structure != null) {
            for (Data element : structure) {
                switch (element) {
                    case QRTYPE:
                        out.append(remNulls(getQrType()) + "\n");
                        break;
                    case VERSION:
                        out.append(remNulls(getFormattedVersion()) + "\n");
                        break;
                    case CODING:
                        out.append(getCodingType() + "\n");
                        break;
                    case ACCOUNT:
                        out.append(remNulls(getIBAN()) + "\n");
                        break;
                    case AMOUNT:
                        if (getAmount() > 0)
                            out.append(formatAmountAsString(getAmount()));
                        out.append("\n");
                        break;
                    case CURRENCY:
                        out.append(remNulls(getCurrency()) + "\n");
                        break;
                    case DUEDATE:
                        int[] dueDate = getDueDate();
                        if (dueDate == null) {
                            out.append("\n");
                        } else {
                            out.append(dueDate[0] + "-" + dueDate[1] + "-" + dueDate[2] + "\n");
                        }
                        break;
                    case REF_TYPE:
                        out.append(remNulls(getReferenceType()) + "\n");
                        break;
                    case REF:
                        out.append(remNulls(getReference()) + "\n");
                        break;
                    case CR_ADDTYPE:
                        out.append(remNulls(getActorAddressType(QRBill.ACTOR_CR)) + "\n");
                        break;
                    case CR_NAME:
                        out.append(remNulls(getActorName(QRBill.ACTOR_CR)) + "\n");
                        break;
                    case CR_ADDRESS1:
                        out.append(remNulls(getActorStreet(QRBill.ACTOR_CR)) + "\n");
                        break;
                    case CR_ADDRESS2:
                        out.append(remNulls(getActorHouseNumber(QRBill.ACTOR_CR)) + "\n");
                        break;
                    case CR_POSTCODE:
                        out.append(remNulls(getActorPostcode(QRBill.ACTOR_CR)) + "\n");
                        break;
                    case CR_LOCATION:
                        out.append(remNulls(getActorLocation(QRBill.ACTOR_CR)) + "\n");
                        break;
                    case CR_COUNTRY:
                        out.append(remNulls(getActorCountry(QRBill.ACTOR_CR)) + "\n");
                        break;
                    case UCR_ADDTYPE:
                        out.append(remNulls(getActorAddressType(QRBill.ACTOR_UCR)) + "\n");
                        break;
                    case UCR_NAME:
                        out.append(remNulls(getActorName(QRBill.ACTOR_UCR)) + "\n");
                        break;
                    case UCR_ADDRESS1:
                        out.append(remNulls(getActorStreet(QRBill.ACTOR_UCR)) + "\n");
                        break;
                    case UCR_ADDRESS2:
                        out.append(remNulls(getActorHouseNumber(QRBill.ACTOR_UCR)) + "\n");
                        break;
                    case UCR_POSTCODE:
                        out.append(remNulls(getActorPostcode(QRBill.ACTOR_UCR)) + "\n");
                        break;
                    case UCR_LOCATION:
                        out.append(remNulls(getActorLocation(QRBill.ACTOR_UCR)) + "\n");
                        break;
                    case UCR_COUNTRY:
                        out.append(remNulls(getActorCountry(QRBill.ACTOR_UCR)) + "\n");
                        break;
                    case UDR_ADDTYPE:
                        out.append(remNulls(getActorAddressType(QRBill.ACTOR_UDR)) + "\n");
                        break;
                    case UDR_NAME:
                        out.append(remNulls(getActorName(QRBill.ACTOR_UDR)) + "\n");
                        break;
                    case UDR_ADDRESS1:
                        out.append(remNulls(getActorStreet(QRBill.ACTOR_UDR)) + "\n");
                        break;
                    case UDR_ADDRESS2:
                        out.append(remNulls(getActorHouseNumber(QRBill.ACTOR_UDR)) + "\n");
                        break;
                    case UDR_POSTCODE:
                        out.append(remNulls(getActorPostcode(QRBill.ACTOR_UDR)) + "\n");
                        break;
                    case UDR_LOCATION:
                        out.append(remNulls(getActorLocation(QRBill.ACTOR_UDR)) + "\n");
                        break;
                    case UDR_COUNTRY:
                        out.append(remNulls(getActorCountry(QRBill.ACTOR_UDR)) + "\n");
                        break;
                    case UNSTR_MSG:
                        out.append(remNulls(getUnstructuredMsg()) + "\n");
                        break;
                    case ALTSCHEMA1:
                        String[] as1 = getAlternativeSchema(-1);
                        out.append(as1[0] + "\n");
                        break;
                    case ALTSCHEMA2:
                        String[] as2 = getAlternativeSchema(-1);
                        out.append(as2[1] + "\n");
                        break;
                    case TRAILER:
                        out.append(remNulls(getTrailer()) + "\n");
                        break;
                    case BILLINFO:
                        out.append(remNulls(getBillInfo()) + "\n");
                        break;
                }
            }
        }
        return out.toString().trim();
    }

    /**
     * Gets the QR Code in the QR Billing object.
     *
     * @return String. The validated QR Bill data. Fields will be separated by new lines. An
     * implementation guide on the format may be found at http://www.paymentstandards.ch/
     * @throws QRBillException Thrown when validation fails. Exception message gives a
     * description of the validation error.
     */
    public String getQRCode() throws QRBillException {
        return toString();
    }

    /**
     * Gets the QR Type Identifier used.
     *
     * @return String. The QR Type Identifier. Possible values: {@link #QRTYPE_SPC}
     */
    public String getQrType () { return this.qrType; }

    /**
     * Gets the QR Bill version number.
     *
     * @return String. The QR Bill version number as a 4-character numerical string, with leading
     * zeros. The first two represent the major version, the second two represent the minor
     * version (e.g. "0100" corresponds to version 1.0).
     */
    public Float getVersion () { return this.version; }

    public String getFormattedVersion () {
        String fv = String.valueOf((int) (this.version * 100));
        if (fv.length() < 4)
            fv = "0" + fv;
        return fv;
    }

    /**
     * Gets the Character Set used.
     *
     * @return String. The Character Set. Possible values: {@link #CODING_LATIN_1}
     */
    public int getCodingType () { return this.codingType; }

    /**
     * Gets the IBAN used.
     *
     * @return String. The IBAN.
     */
    public String getIBAN () { return this.account; }

    /**
     * Gets the amount payable.
     *
     * @return Float. Returns the amount payable as a float, accurate to two decimal places. If a
     * value of -1 is returned then there is no amount payable registered.
     */
    public float getAmount () { return this.amount; }

    /**
     * Gets the currency.
     *
     * @return String. Returns the currency of the amount payable. Possible values:
     * {@link #CURRENCY_CHF}, {@link #CURRENCY_EUR}
     */
    public String getCurrency () { return this.currency; }

    /**
     * Gets the reference type. Note that if this is {@link #REFTYPE_NON}, the value for reference
     * will be blank.
     *
     * @return String. Returns the reference type. Possible values: {@link #REFTYPE_QRR},
     * {@link #REFTYPE_SCOR}, {@link #REFTYPE_NON}.
     */
    public String getReferenceType () { return this.referenceType; }

    /** Gets the bill reference. Note that if the value for reference type is {@link #REFTYPE_NON},
     * the value for this will be blank.
     *
     * @return tring. Returns the reference. This can be up to 27 characters long or blank.
     */
    public String getReference () { return this.reference; }

    /**
     * Gets any additional information. Deprecated - use getUnstructuredMsg instead.
     *
     * This method is a copy of the #getUnstructuredMsg method, which uses the same termenology
     * used from version 2.0 of the specification. As such it is deprecated and will be removed
     * in the future.
     *
     * @return String. The additional bill information.
     */
    @Deprecated
    public String getAdditionalInfo () { return getUnstructuredMsg(); }

    /**
     * Gets the name of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor name, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorName (int actorType) { return this.actors[actorType].name; }

    /**
     * Gets the address type of the specified actor.
     *
     * @param actorType actorType Integer. The actor type being queried. Possible values:
     * {@link #ACTOR_CR}, {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor address type. Possible values: {@link #ADDTYPE_STRUCTURED},
     * {@link #ADDTYPE_COMBINED}.
     */
    public String getActorAddressType (int actorType) { return this.actors[actorType].addressType; }

    /**
     * Gets the street address of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor street address, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorStreet (int actorType) { return this.actors[actorType].address1; }

    /**
     * Gets the house number of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor house number, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorHouseNumber (int actorType) { return this.actors[actorType].address2; }

    /**
     * Gets the postcode of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor postcode, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorPostcode (int actorType) { return this.actors[actorType].postcode; }

    /**
     * Gets the location (town, city, etc) of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor location, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorLocation (int actorType) { return this.actors[actorType].location; }

    /**
     * Gets the country of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor country, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorCountry (int actorType) { return this.actors[actorType].country; }

    /**
     * Gets any unstructured message included.
     *
     * @return String. The unstructured message.
     */
    public String getUnstructuredMsg() { return this.unstructuredMsg; }

    /**
     * Gets the Trailer, which is the unambiguous indicator for the end of payment data.
     *
     * @return String. Possible values: {@link #TRAILER_EPD}.
     */
    public String getTrailer() { return this.trailer; }

    /**
     * Gets the bill information which can be used as coded information for automated
     * booking of the payment.
     *
     * @return String. The bill information.
     */
    public String getBillInfo() { return this.billInfo; }

    /**
     * Gets all Alternative Schemas.
     *
     * @return String Array. If unprocessed, a string array of up to two String elements, denoting
     * the two lines as presented in the QR code will be returned.
     */
    public String[] getAlternativeSchema () {
        return getAlternativeSchema (-1);
    }

    /**
     * Gets a specified Alternative Schema.
     *
     * @param index Integer. The index being entered - either 0 or 1. Any other value will return all
     *              schemas unprocessed (see below) as a two element String array.
     *
     * @return String Array. If processed a string array  beginning with the two-character schema
     * identifier as the first element, the single-character deliminator as the second and
     * subsequent elements will be the individual schema data, processed using the deliminator. If
     * unprocessed, a string array of up to two String elements, denoting the two lines as presented
     * in the QR code will be returned.
     */
    public String[] getAlternativeSchema (int index) {
        if (index > 1 || index < 0) {
            return this.as;
        } else {
            if (this.as[index] == null || this.as[index].length() < 3) {
                return null;
            } else {
                String token = this.as[index].substring(0, 2);
                String del = String.valueOf(this.as[index].charAt(2));
                String[] data = this.as[index].substring(3).split(del);
                String[] retArray = new String[data.length + 2];
                retArray[0] = token;
                retArray[1] = del;
                for (int i = 2; i < retArray.length; i++)
                    retArray[i] = data[i - 2];
                return retArray;
            }
        }
    }

    /**
     * Gets the due date, if available.
     *
     * @return Integer Array. Three-element array, giving the year, month and day, in that order. If
     * no value is available, null is returned.
     */
    public int[] getDueDate () {
        if (this.dueDate == null || this.dueDate.length() == 0) {
            return null;
        } else {
            String[] tempDate = this.dueDate.split("-");
            if (tempDate.length != 3) {
                return null;
            } else {
                try {
                    int year = Integer.parseInt(tempDate[0]);
                    int month = Integer.parseInt(tempDate[1]);
                    int day = Integer.parseInt(tempDate[2]);
                    return new int[] { year, month, day};
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
    }

    /**
     * Sets the QR Type Identifier used. Required for a valid QR Bill.
     *
     * @param qrType String. The QR Type Identifier. Possible values: {@link #QRTYPE_SPC}
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setQrType (String qrType) {
        if (qrType != null && qrType.equalsIgnoreCase(QRBill.QRTYPE_SPC)) {
            this.qrType = qrType.toUpperCase();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the QR Bill version number directly from an input of a 4-character numerical string, as
     * described in the specification. Required for a valid QR Bill.
     *
     * @param version String. The QR Bill version number as a 4-character numerical string, with
     *                leading zeros. The first two represent the major version, the second two
     *                represent the minor version (e.g. "0100" corresponds to version 1.0).
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setVersion (String version) {
        if (version == null || version.length() != 4) {
            return false;
        } else {
            try {
                Float v = Float.parseFloat(version) / 100;
                return setVersion(v);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    /**
     * Sets the QR Bill version number directly from an float input, as Required for a valid
     * QR Bill.
     *
     * @param version Float. The QR Bill version number as a float.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setVersion (Float version) {
        if (version <= QRBill.VERSION_SUPPORTED) {
            this.version = version;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the Character Set used. Required for a valid QR Bill.
     *
     * @param codingType String. The Character Set. Possible values: {@link #CODING_LATIN_1}
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setCodingType (int codingType) {
        int[] codingTypes = new int[] { QRBill.CODING_LATIN_1 };
        boolean valid = false;
        for (int code : codingTypes) {
            if (codingType == code)
                valid = true;
        }
        if (valid) {
            this.codingType = codingType;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the IBAN used. Required for a valid QR Bill.
     *
     * @param iban String. A valid IBAN. Maximum length of 21 characters.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setIBAN (String iban) {
        iban = iban.replace(" ","").trim();
        for (int i = 0; i < iban.length(); i++)
            if ("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(iban.toUpperCase().charAt(i)) == -1)
                return false;
        this.account = validateStr(iban, true, 21);
        if (this.account == null) {
            return false;
        } else {
            if (this.account.toUpperCase().startsWith("CH")) {
                return true;
            } else if (this.account.toUpperCase().startsWith("LI")) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Sets the amount payable to empty - the same as setting it to a value of -1.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setAmount () {
        return setAmount (-1F);
    }

    /**
     * Sets the amount payable.
     *
     * @param amt Float. The amount payable as a float, accurate to two decimal places. If a value
     *            of -1 is used then the amount payable will be set as empty.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setAmount (float amt) {
        if (amt < 0) {
            this.amount = -1;
            return true;
        } else {
            String amount = formatAmountAsString(amt);
            this.amount = Float.parseFloat(amount);
            if (amount.length() > 12) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Sets the currency. Required for a valid QR Bill.
     *
     * @param currency String. The currency of the amount payable. Possible values:
     *                 {@link #CURRENCY_CHF}, {@link #CURRENCY_EUR}
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setCurrency(String currency) {
        if ((currency == null || currency.length() == 0)) {
            return false;
        } else {
            String[] currencies = new String[] {QRBill.CURRENCY_CHF, QRBill.CURRENCY_EUR};
            boolean valid = false;
            for (String cur : currencies) {
                if (currency.equalsIgnoreCase(cur))
                    valid = true;
            }
            if (valid)
                this.currency = currency.toUpperCase();
            return valid;
        }
    }

    /**
     * Sets the due date of the QR bill.
     *
     * @param year Integer. The year, as 4-digit number.
     * @param month Integer. The month, as a number between 1 (January) and 12 (December).
     * @param day Integer. The month day.
     *
     * @return Boolean. Always returns true, even if the value is not stored.
     */
    public boolean setDueDate (int year, int month, int day) {
        boolean isValid = true;

        if (year < 2018 || year > 9999)
            isValid = false;

        if (month < 1 || month > 12)
            isValid = false;

        int[] maxDays = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (day < 1 || day > maxDays[month - 1])
            isValid = false;

        if (month == 2 && day == 29) {
            if (year % 4 > 0)
                isValid = false;
            if (year % 100 == 0 && year % 400 > 0)
                isValid = false;
        }

        if (isValid) {
            this.dueDate = year + "-"
                    + ("00" + String.valueOf(month)).substring(String.valueOf(month).length())
                    + "-" + ("00" + String.valueOf(day)).substring(String.valueOf(day).length());
        } else {
            this.dueDate = "";
        }
        return true;
    }

    /**
     * Sets the reference type as {@link #REFTYPE_NON}, wuth an empty reference.
     *
     * @return Always returns true.
     */
    public boolean setReference () {
        return setReference (QRBill.REFTYPE_NON, null);
    }

    /**
     * Sets the reference type and reference itslef.
     *
     * @param refType String. The currency of the amount payable. Possible values:
     * {@link #REFTYPE_QRR}, {@link #REFTYPE_SCOR}, {@link #REFTYPE_NON}
     * @param ref String. The reference. In the case of {@link #REFTYPE_QRR} and
     * {@link #REFTYPE_SCOR}, this must represent valid QRR and SCOR references, of maximum
     *            27 annd 25 characters lengths respectively. For {@link #REFTYPE_NON}, this
     *            value will be ignored and the reference left empty.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setReference (String refType, String ref) {
        boolean valid = false;
        String[] refTypes = new String[]{QRBill.REFTYPE_QRR, QRBill.REFTYPE_SCOR, QRBill.REFTYPE_NON};
        for (String type : refTypes) {
            if (refType != null && refType.equalsIgnoreCase(type))
                valid = true;
        }
        if (valid)
            this.referenceType = refType.toUpperCase();

        if (ref != null)
            ref = ref.replace(" ","").trim();
        switch (refType) {
            case QRBill.REFTYPE_QRR:
                this.reference = validateStr(ref, true, 27);
                if (this.reference == null || !Modulo10.validate(this.reference))
                    valid = false;
                break;
            case QRBill.REFTYPE_SCOR:
                this.reference = validateStr(ref, true, 25);
                if (this.reference == null)
                    valid = false;
                break;
            case QRBill.REFTYPE_NON:
                this.reference = "";
                break;
            default:
                valid = false;
        }

        return valid;
    }

    /**
     * Sets the additional information field. Deprecated - use getUnstructuredMsg instead.
     *
     * This method is a copy of the #setUnstructuredMsg method, which uses the same termenology
     * used from version 2.0 of the specification. As such it is deprecated and will be removed
     * in the future.
     *
     * @param info String. The text of the additional information. Maximum length of 140 characters.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    @Deprecated
    public boolean setAdditionalInfo(String info) {
        return setUnstructuredMsg(info);
    }

    /**
     * Sets an unstructured message.
     *
     * @param unstructuredMsg String. The text of the unstructured message (version 2.0+, formerly
     * known as additional info in version 1.0). Maximum length of 140 characters.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setUnstructuredMsg(String unstructuredMsg) {
        this.unstructuredMsg = validateStr(unstructuredMsg, false, 140);
        return unstructuredMsg.length() > 140;
    }

    /**
     * Unambiguous indicator for the end of payment data. Fixed value "EPD" (End Payment Data), so
     * the method realistically does not allow anything other than that value at present.
     *
     * @param trailer String. Only poaaible value, currently, is {@link #TRAILER_EPD}.
     *
     * @return Whether the value has validated and stored correctly or not.
     */
    public boolean setTrailer(String trailer) {
        if (trailer == null)
            return false;
        trailer = trailer.toUpperCase().trim();
        if (trailer.length() != 3)
            return false;

        switch (trailer) {
            case TRAILER_EPD:
                this.trailer = trailer;
                return true;
            default:
                return false;
        }
    }

    /**
     * Coded information for automated booking of the payment. The format of this information is not
     * part of the standardization, but reccomendations on this may be found in annex E of the
     * implementation guidelines. Only string length is checked in this implementation.
     *
     * @param billInfo String. The text of the billing information. Maximum length of 140 characters.
     *
     * @return Whether the value has validated and stored correctly or not.
     */
    public boolean setBillInfo(String billInfo) {
        this.billInfo = validateStr(billInfo, false, 140);
        return billInfo.length() > 140;
    }

    /**
     * Stores and validates an inputted Alternative Schema in up to two lines. The first two
     * characters of the first/only line should be an identifier for the schema, the third
     * character will be treated as the delineator for the rest of the string data.
     *
     * Due to the ambiguity of implementation guide as well as the apparent error in the example in
     * the same document, clarification was sought on how this data is handled. From this response
     * we can say the following:
     *
     * <ul>
     *     <li>
     *         Up to two alternate schemas may exist, each taking up a single line in the QR code.
     *         An alternate schema entry is not mandatory.
     *     </li>
     *     <li>
     *         Each alternate schemas entry may be up to 100 characters in length.
     *     </li>
     * </ul>
     *
     * @param data String. The alternative schema data as two entries. Maximum length of each entry
     *            is 100 characters, no maximum length is specified for the second. If null the entry
     *            is cleared.
     * @param index Integer. The index being entered - either 0 or 1.
     *
     * @return Boolean. Always returns true as, even if not set, the field is not mandatory.
     */
    public boolean setAlternativeSchema(String data, int index) {
        if (data == null || index < 0 || index > 1) {
            return false;
        } else {
            this.as[index] = validateStr(data, false, 100);
            return true;
        }
    }

    /**
     * Stores and validates an inputted Alternative Schema in up to two lines. The first two
     * characters of the first/only line should be an identifier for the schema, the third
     * character will be treated as the delineator for the rest of the string data.
     *
     * Due to the ambiguity of implementation guide as well as the apparent error in the example in
     * the same document, clarification was sought on how this data is handled. From this response
     * we can say the following:
     *
     * <ul>
     *     <li>
     *         Up to two alternate schemas may exist, each taking up a single line in the QR code.
     *         An alternate schema entry is not mandatory.
     *     </li>
     *     <li>
     *         Each alternate schemas entry may be up to 100 characters in length.
     *     </li>
     * </ul>
     *
     * @param data String Array. The alternative schema data as an array. Maximum length of each entry
     *            is 100 characters, no maximum length is specified for the second. If the array only contains
     *             one element then this is assigned as the first and the second is cleared. If the array
     *             contains more than two elements then all elements after the second are ignored. If null all
     *             entries are cleared.
     *
     * @return Boolean. Always returns true as, even if not set, the field is not mandatory.
     */
    public boolean setAlternativeSchema(String[] data) {
        if (data == null) {
            this.as[0] = "";
            this.as[1] = "";
        } else for (int i = 0; i < data.length; i++) {
            if (!setAlternativeSchema(data[i], i))
                return false;
        }
        return true;
    }

    /**
     * Clears the Alternative Schema entries.
     *
     * @return Boolean. Always returns true as, even if not set, the field is not mandatory.
     */
    public boolean setAlternativeSchema() {
        return setAlternativeSchema(null);
    }

    /**
     * Sets an actor associated with the QR Bill. An actor is one of several legal entities affected
     * by the QR Bill, namely the creditor, ultimate creditor or ultimate debtor.
     *
     * This method is complient with the version 1.0 of the specification.
     *
     * @param typeId Integer. The actor type. Possible values:
     *               {@link #ACTOR_CR}, {@link #ACTOR_UCR}, {@link #ACTOR_UDR}
     * @param name String. Mandatory. Maximum length of 70 characters. The actor full name.
     * @param street String. Optional. Maximum length of 70 characters. The actor street address.
     * @param housenumber String. Optional. Maximum length of 16 characters. The actor house number.
     * @param postalcode String. Optional. Maximum length of 16 characters. The actor post code.
     * @param location String. Mandatory. Maximum length of 35 characters. The actor location,
     *                 such as town or city.
     * @param country  String. Mandatory. Maximum length of 2 characters. The actor country, given
     *                 as a 2-letter country code (ISO 3166-1).
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setActor(int typeId, String name, String street, String housenumber, String postalcode, String location, String country) {
        return setActor(typeId, name, null, street, housenumber, postalcode, location, country);
    }

    /**
     * Sets an actor associated with the QR Bill. An actor is one of several legal entities affected
     * by the QR Bill, namely the creditor, ultimate creditor or ultimate debtor.
     *
     * This method is complient with the versions 1.0 to 2.0 of the specification.
     *
     * @param typeId Integer. The actor type. Possible values:
     *               {@link #ACTOR_CR}, {@link #ACTOR_UCR}, {@link #ACTOR_UDR}
     * @param name String. Mandatory. Maximum length of 70 characters. The actor full name.
     * @param addressType String. Mandatory. The format used for the actor address.
     * @param address1 String. Dependant. Maximum length of 70 characters. The optional actor street address
     *                 or mandatory actor street address and house number, depending on version and Address Type.
     * @param address2 String. Dependant. Maximum length of 16 characters. The actor optional house
     *                 number ot mandatory actor post code and location, depending on version and Address Type.
     * @param postalcode String. Optional. Maximum length of 16 characters. The actor post code.
     * @param location String. Dependant. Maximum length of 35 characters. The actor location,
     *                 such as town or city. Not manditory if the address type is consolidated.
     * @param country  String. Mandatory. Maximum length of 2 characters. The actor country, given
     *                 as a 2-letter country code (ISO 3166-1).
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setActor(int typeId, String name, String addressType, String address1, String address2, String postalcode, String location, String country) {
        if (typeId != QRBill.ACTOR_CR && typeId != QRBill.ACTOR_UCR && typeId != QRBill.ACTOR_UDR) {
            return false;
        } else {
            this.actors[typeId].name = validateStr(name, true, 70);
            if (addressType != null)
                this.actors[typeId].addressType = validateStr(addressType, this.version >= 2.0F, 1);
            this.actors[typeId].address1 = validateStr(address1, false, 70);
            this.actors[typeId].address2 = validateStr(address2, false,
                    this.actors[typeId].addressType.equals(ADDTYPE_COMBINED) ? 70 : 16);
            this.actors[typeId].postcode = validateStr(postalcode, true, 16);
            this.actors[typeId].location = validateStr(location, true, 35);
            this.actors[typeId].country = validateStr(country, true, 2);

            return validateDependancies(typeId);
        }
    }

    private String remNulls (String raw) {
        return raw == null ? "" : raw;
    }

    private ArrayList<QRBillException> validateData (String rawData) {
        ArrayList<QRBillException> errors = new ArrayList<QRBillException>();
        if (rawData == null || rawData.length() == 0)
            errors.add(new QRBillException(1, "Input data empty or null."));

        if (rawData.length() > 997)
            errors.add(new QRBillException(2, "Input data exceeds maximum allowed limit."));

        String[] qrData = rawData.trim().split("\n");
        if (qrData.length < 25)
            errors.add(new QRBillException(3, "Malformed Data - insufficient fields."));

        if (!setVersion(qrData[1]))
            errors.add(new QRBillException(4, "Version invalid or not supported"));

        this.actors[0] = new Actor(QRBill.ACTOR_CR);
        this.actors[1] = new Actor(QRBill.ACTOR_UCR);
        this.actors[2] = new Actor(QRBill.ACTOR_UDR);
        Data[] structure = getStructure();
        String refType = QRBill.REFTYPE_NON;
        Data[] actorIds;

        for (int i = 0; i < qrData.length; i++) {
            String item = qrData[i];
            Data key = structure.length > i ? structure[i] : Data.NONE;
            switch (key) {
                case NONE:
                case VERSION:
                    // Ignore
                    break;
                case QRTYPE:
                    if (!setQrType(item))
                        errors.add(new QRBillException(5, "QR Type invalid or not supported"));
                    break;
                case CODING: // Coding Type
                    if (!setCodingType(item))
                        errors.add(new QRBillException(6, "Valid Coding type Missing"));
                    break;
                case ACCOUNT: // Konto
                    if (!setIBAN(item))
                        errors.add(new QRBillException(7, "Valid IBAN Missing"));
                    break;
                case CR_NAME:
                case UCR_NAME:
                case UDR_NAME:
                    actorIds = new Data[] {Data.CR_NAME, Data.UCR_NAME, Data.UDR_NAME};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == key) {
                            this.actors[j].name = validateStr(item, true, 70);
                        }
                    }
                    break;
                case CR_ADDRESS1:
                case UCR_ADDRESS1:
                case UDR_ADDRESS1:
                    actorIds = new Data[] {Data.CR_ADDRESS1, Data.UCR_ADDRESS1, Data.UDR_ADDRESS1};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == key) {
                            this.actors[j].address1 = validateStr(item, false, 70);
                        }
                    }
                    break;
                case CR_ADDRESS2:
                case UCR_ADDRESS2:
                case UDR_ADDRESS2:
                    actorIds = new Data[] {Data.CR_ADDRESS2, Data.UCR_ADDRESS2, Data.UDR_ADDRESS2};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == key && this.actors[j].addressType != null) {
                            this.actors[j].address2 = validateStr(item, false,
                                    this.actors[j].addressType.equals(ADDTYPE_COMBINED) ? 70 : 16);
                        }
                    }
                    break;
                case CR_POSTCODE:
                case UCR_POSTCODE:
                case UDR_POSTCODE:
                    actorIds = new Data[] {Data.CR_POSTCODE, Data.UCR_POSTCODE, Data.UDR_POSTCODE};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == key) {
                            this.actors[j].postcode = validateStr(item, true, 16);
                        }
                    }
                    break;
                case CR_LOCATION:
                case UCR_LOCATION:
                case UDR_LOCATION:
                    actorIds = new Data[] {Data.CR_LOCATION, Data.UCR_LOCATION, Data.UDR_LOCATION};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == key) {
                            this.actors[j].location = validateStr(item, true, 35);
                        }
                    }
                    break;
                case CR_COUNTRY:
                case UCR_COUNTRY:
                case UDR_COUNTRY:
                    actorIds = new Data[] {Data.CR_COUNTRY, Data.UCR_COUNTRY, Data.UDR_COUNTRY};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == key) {
                            this.actors[j].country = validateStr(item.toUpperCase(), true, 2);
                        }
                    }
                    break;
                case CR_ADDTYPE:
                case UCR_ADDTYPE:
                case UDR_ADDTYPE:
                    actorIds = new Data[] {Data.CR_ADDTYPE, Data.UCR_ADDTYPE, Data.UDR_ADDTYPE};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == key) {
                            this.actors[j].addressType = validateStr(item.toUpperCase(), this.version >= 2.0F, 1);
                        }
                    }
                    break;
                case AMOUNT:
                    setAmount(item);
                    break;
                case CURRENCY:
                    if (!setCurrency(item))
                        errors.add(new QRBillException(8, "Valid Currency Missing"));
                    break;
                case DUEDATE:
                    setDueDate(item);
                    break;
                case REF_TYPE:
                    refType = item;
                    break;
                case REF:
                    if (!setReference(refType, item))
                        errors.add(new QRBillException(9, "Valid Reference Missing"));
                    break;
                case ALTSCHEMA1:
                    setAlternativeSchema(item, 0);
                    break;
                case ALTSCHEMA2:
                    setAlternativeSchema(item, 1);
                    break;
                case UNSTR_MSG:
                    setUnstructuredMsg(item);
                    break;
                case TRAILER:
                    setTrailer(item);
                    break;
                case BILLINFO:
                    setBillInfo(item);
                    break;
            }
        }

        if (getReference() == null)
            setReference(QRBill.REFTYPE_NON, "");

        boolean allGood = true;
        for (int i = 0; i < this.actors.length; i++)
            if (!validateDependancies(i))
                allGood = false;

        if (!allGood)
            errors.add(new QRBillException(10, "Mandatory actor dependancies not met."));

        return null;

    }

    private boolean setCodingType (String codingType) {
        try {
            int type = Integer.parseInt(codingType);
            return setCodingType(type);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean setAmount (String amt) {
        try {
            return setAmount(Float.parseFloat(amt));
        } catch (NumberFormatException e) {
            return setAmount(-1.0f);
        }
    }

    private boolean setDueDate (String dueDate) {
        if (dueDate == null || dueDate.length() == 0) {
            return setDueDate (0, 0, 0);
        } else {
            String[] tempDate = dueDate.split("-");
            if (tempDate.length != 3) {
                return setDueDate (0, 0, 0);
            } else {
                try {
                    int year = Integer.parseInt(tempDate[0]);
                    int month = Integer.parseInt(tempDate[1]);
                    int day = Integer.parseInt(tempDate[2]);
                    return setDueDate (year, month, day);
                } catch (NumberFormatException e) {
                    return setDueDate (0, 0, 0);
                }
            }
        }
    }

    private String validateStr (String entry, boolean required) {
        return validateStr (entry, required, 0);
    }

    private String validateStr (String entry, boolean required, int maxLen) {
        if (entry == null || entry.length() == 0) {
            if (required) {
                return null;
            } else {
                return "";
            }
        } else if (maxLen == 0 || maxLen >= entry.length()) {
            return entry;
        } else {
            if (required) {
                return null;
            } else {
                return "";
            }
        }
    }

    private boolean validateDependancies (int typeId) {
        if (typeId > 2 || typeId < 0)
            return false;

        StringBuffer test = new StringBuffer();
        if (this.actors[typeId].name != null) test.append(this.actors[typeId].name.trim());
        if (this.actors[typeId].address1 != null) test.append(this.actors[typeId].address1.trim());
        if (this.actors[typeId].address2 != null) test.append(this.actors[typeId].address2.trim());
        if (this.actors[typeId].postcode != null) test.append(this.actors[typeId].postcode.trim());
        if (this.actors[typeId].location != null) test.append(this.actors[typeId].location.trim());
        if (this.actors[typeId].country != null) test.append(this.actors[typeId].country.trim());
        boolean hasEntry = test.length() > 0;
        boolean valid = true;

        if (hasEntry) {
            if (this.actors[typeId].name == null || this.actors[typeId].name.length() == 0)
                valid = false;
            if (this.version >= 2.0F) {
                if (this.actors[typeId].addressType == null || this.actors[typeId].addressType.length() == 0) {
                    valid = false;
                } else switch (this.actors[typeId].addressType) {
                    case ADDTYPE_STRUCTURED:
                        if (this.actors[typeId].address1 == null || this.actors[typeId].address1.length() == 0)
                            valid = false;
                        if (this.actors[typeId].postcode == null || this.actors[typeId].postcode.length() == 0)
                            valid = false;
                        if (this.actors[typeId].location == null || this.actors[typeId].location.length() == 0)
                            valid = false;
                        if (this.actors[typeId].country == null || this.actors[typeId].country.length() == 0)
                            valid = false;
                        break;
                    case ADDTYPE_COMBINED:
                        if (this.actors[typeId].address1 == null || this.actors[typeId].address1.length() == 0)
                            valid = false;
                        if (this.actors[typeId].address2 == null || this.actors[typeId].address2.length() == 0)
                            valid = false;
                        break;
                    default:
                        valid = false;
                }
            } else {
                if (this.actors[typeId].postcode == null || this.actors[typeId].postcode.length() == 0)
                    valid = false;
                if (this.actors[typeId].location == null || this.actors[typeId].location.length() == 0)
                    valid = false;
                if (this.actors[typeId].country == null || this.actors[typeId].country.length() == 0)
                    valid = false;
            }
        } else if (typeId == QRBill.ACTOR_CR) {
            valid = false;
        }

        if (valid && typeId > 0) {
            if (this.actors[typeId].addressType == null)
                this.actors[typeId].addressType = "";
            if (this.actors[typeId].name == null)
                this.actors[typeId].name = "";
            if (this.actors[typeId].address1 == null)
                this.actors[typeId].address1 = "";
            if (this.actors[typeId].address2 == null)
                this.actors[typeId].address2 = "";
            if (this.actors[typeId].postcode == null)
                this.actors[typeId].postcode = "";
            if (this.actors[typeId].location == null)
                this.actors[typeId].location = "";
            if (this.actors[typeId].country == null)
                this.actors[typeId].country = "";
        }

        return valid;
    }

    private String formatAmountAsString (Float amt) {
        int pointPos = -1;
        String amount = String.valueOf(amt);
        for (int i = 0; i < amount.length(); i++) {
            try {
                Integer.parseInt(String.valueOf(amount.charAt(i)));
            } catch (NumberFormatException e) {
                pointPos = i;
                break;
            }
        }
        String decimals = ".00";
        if (pointPos > -1)
            decimals = amount.substring(pointPos + 1);
        if (decimals.length() > 2) {
            amount = amount.substring(0, amount.length() - decimals.length() + 2);
        } else if (decimals.length() == 1) {
            amount += "0";
        }
        return amount;
    }

    private Data[] getStructure () {
        if (this.version == null)
            return null;

        if (this.version == 2.00f) {
            return this.version2;
        } else if (this.version >= 1.00f) {
            return this.version1;
        } else {
            this.version = null;
            return null;
        }
    }

    /**
     * Exception thrown upon validation failure whenever the class is instantiated with raw QR code
     * or {@link #getQRCode} is called. This exception object contains both an error code (int) and
     * error message (String).
     *
     * The error codes and messages that may be returned are as follows:
     *
     * <ol>
     *  <li>Input data empty or null.
     *  <li>Input data exceeds maximum allowed limit.
     *  <li>Malformed Data - insufficient fields.
     *  <li>Version invalid or not supported.
     *  <li>QR Type invalid or not supported.
     *  <li>Valid Coding type Missing.
     *  <li>Valid IBAN Missing.
     *  <li>Valid Currency Missing.
     *  <li>Valid Reference Missing.
     *  <li>Mandatory actor dependencies not met.
     * </ol>
     */
    public class QRBillException extends Exception {
        private int errorId;

        /**
         * Constructor for instantiating a QRBillException object.
         *
         * @param errorId Integer. A custom identifying error ID code.
         * @param msg String. The error description (overrides {@link java.lang.Exception}).
         */
        public QRBillException(int errorId, String msg) {
            super(msg);
            this.errorId = errorId;
        }

        /**
         * Returns a code corrisponding to the exception caught.
         *
         * @return Integer. The error ID code.
         */
        public int getErrorId () {
            return this.errorId;
        }
    }

    private class Actor {
        public String name = "";
        public String addressType = QRBill.ADDTYPE_STRUCTURED;
        public String address1 = "";
        public String address2 = "";
        public String postcode = "";
        public String location = "";
        public String country = "";

        private int typeId;

        public Actor (int type) {
            this.typeId = type;
            this.addressType = QRBill.ADDTYPE_STRUCTURED;
        }

        public int getType() {
            return this.typeId;
        }
    }

    private static class Modulo10 {
        private static final int[][] pattern = {
                { 0, 9, 4, 6, 8, 2, 7, 1, 3, 5 },
                { 9, 4, 6, 8, 2, 7, 1, 3, 5, 0 },
                { 4, 6, 8, 2, 7, 1, 3, 5, 0, 9 },
                { 6, 8, 2, 7, 1, 3, 5, 0, 9, 4 },
                { 8, 2, 7, 1, 3, 5, 0, 9, 4, 6 },
                { 2, 7, 1, 3, 5, 0, 9, 4, 6, 8 },
                { 7, 1, 3, 5, 0, 9, 4, 6, 8, 2 },
                { 1, 3, 5, 0, 9, 4, 6, 8, 2, 7 },
                { 3, 5, 0, 9, 4, 6, 8, 2, 7, 1 },
                { 5, 0, 9, 4, 6, 8, 2, 7, 1, 3 }
        };

        private static final int[] checkDigits = {
                0, 9, 8, 7, 6, 5, 4, 3, 2, 1
        };

        private static final int codeLength = 27;

        public static boolean validate(String input) {
            if (input != null && input.length() > 0) {
                input = input.replaceAll(" ", "").trim();
            }

            if (input == null) return false;

            try {
                int check = getCheckDigit(input);
                String td = input.substring(input.length() - 1);
                int endDigit = Integer.parseInt(td);
                return endDigit == check;
            } catch (Exception e) {
                return false;
            }
        }

        public static int getCheckDigit(String input) {
            if (input.length() < codeLength) return -1;

            String bd = input.substring(0, input.length() - 1);

            int position = 0;
            try {
                for (int i = 0; i < bd.length(); i++) {
                    int digit = Integer.parseInt((String) Character.toString(bd.charAt(i)));
                    position = pattern[position][digit];
                }
                return checkDigits[position];
            } catch (Exception e) {
                return -1;
            }
        }
    }

}