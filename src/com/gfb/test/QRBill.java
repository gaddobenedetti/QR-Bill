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

/**
 * <h1>Swiss Payments Code Serializer</h1>
 *
 * <P>This class was designed to be a bare bones serializer for the Swiss Payments Code, which is
 * meant to adapt the payment system in Switzerland and Liechtenstein to the international ISO 20022
 * standard. For more information on this please consult the
 * {@link <a href="http://www.paymentstandards.ch/">PaymentStandards.ch</a>} site, as the
 * {@link <a href="https://www.paymentstandards.ch/dam/downloads/ig-qr-bill-en.pdf/">implementation
 * guide</a>} here was the basis for this class.</P>
 *
 * <P>This class was purposely written using only core Java and without any imports, so that it can
 * more easily adapted to other languages and platforms. As such it does not implement
 * {@link java.io.Serializable}, out of the box, but will do so if simply added. It is designed, not
 * only to serialize, but also to validate data according to the above standard.</P>
 *
 * @author  Gaddo F Benedetti
 * @version 1.1
 * @since   2018-05-19
 */

public class QRBill {

    private boolean strict;
    private String qrType;
    private String version;
    private int codingType;
    private String konto;
    private float betrag;
    private String wahrung;
    private String zahlbarBis;
    private String referenztyp;
    private String referenz;
    private String zusInfo = "";
    private String[] av = new String[] { "", ""};
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


    private static final float      VERSION_SUPPORTED   = 1.00F;

    /**
     * Constructor that generates a partially empty QR Billing object, calling for strict
     * validation and a number of default values. These values are:
     * <ul>
     *     <li>QR Type Identifier: {@link #QRTYPE_SPC}</li>
     *     <li>Version: {@value #VERSION_SUPPORTED}</li>
     *     <li>Character Set: {@link #CODING_LATIN_1}</li>
     *     <li>Reference Type: {@link #REFTYPE_NON}</li>
     *     <li>Amount: None</li>
     *     <li>Currency: {@link #CURRENCY_CHF}</li>
     * </ui>
     */
    public QRBill() {
        this(true);
    }

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
     * </ui>
     *
     * @param strict Boolean. Whether validation should be strict or not. If set to false,
     *               various constraints, such as field length, are ignored, although validation
     *               will still occur.
     */
    public QRBill(boolean strict) {
        this.strict = strict;
        setQrType(QRBill.QRTYPE_SPC);
        int version = (int) (QRBill.VERSION_SUPPORTED * 100);
        setVersion(("0000" + String.valueOf(version))
                .substring(String.valueOf(version).length()));
        setCodingType(1);
        setReference();
        setAmount();
        setCurrency(QRBill.CURRENCY_CHF);
    }

    /**
     * Constructor that generates a QR Billing object, calling for strict validation and using raw
     * QR Bill data as input.
     *
     * @param rawData String. Basic QR Bill data. Fields should be separated by new lines. An
     *                implementation guide on the format may be found at
     *                http://www.paymentstandards.ch/
     * @throws QRBillException Thrown when validation fails. Exception message gives a
     * description of the validation error.
     */
    public QRBill(String rawData) throws QRBillException {
        this(rawData, true);
    }

    /**
     * Constructor that generates a QR Billing object, using raw QR Bill data as input.
     *
     * @param rawData String. Basic QR Bill data. Fields should be separated by new lines. An
     *                implementation guide on the format may be found at
     *                http://www.paymentstandards.ch/
     * @param strict Boolean. Whether validation should be strict or not. If set to false,
     *               various constraints, such as field length, are ignored, although validation
     *               will still occur.
     * @throws QRBillException Thrown when validation fails. Exception message gives a
     * description of the validation error.
     */
    public QRBill(String rawData, boolean strict) throws QRBillException {
        this.strict = strict;
        String error = validateData (rawData, strict);
        if (error != null && strict)
            throw new QRBillException(error);
    }

    /**
     * Returns the Unvalidated QR Code in the QR Billing object.
     *
     * @return String. The unvalidated QR Bill data. Fields will be separated by new lines. An
     * implementation guide on the format may be found at http://www.paymentstandards.ch/
     */
    @Override
    public String toString () {
        StringBuffer out = new StringBuffer();
        out.append(getQrType() + "\n");
        out.append(getVersion() + "\n");
        out.append(getCodingType() + "\n");
        out.append(getIBAN() + "\n");

        out.append(getActorName(QRBill.ACTOR_CR) + "\n");
        out.append(getActorStreet(QRBill.ACTOR_CR) + "\n");
        out.append(getActorHouseNumber(QRBill.ACTOR_CR) + "\n");
        out.append(getActorPostcode(QRBill.ACTOR_CR) + "\n");
        out.append(getActorLocation(QRBill.ACTOR_CR) + "\n");
        out.append(getActorCountry(QRBill.ACTOR_CR) + "\n");

        out.append(getActorName(QRBill.ACTOR_UCR) + "\n");
        out.append(getActorStreet(QRBill.ACTOR_UCR) + "\n");
        out.append(getActorHouseNumber(QRBill.ACTOR_UCR) + "\n");
        out.append(getActorPostcode(QRBill.ACTOR_UCR) + "\n");
        out.append(getActorLocation(QRBill.ACTOR_UCR) + "\n");
        out.append(getActorCountry(QRBill.ACTOR_UCR) + "\n");

        if (getAmount() > 0)
            out.append(formatAmountAsString(getAmount()));
        out.append("\n");

        out.append(getCurrency() + "\n");
        int[] dueDate = getDueDate();
        if (dueDate == null) {
            out.append("\n");
        } else {
            out.append(dueDate[0] + "-" + dueDate[1] + "-" + dueDate[2] + "\n");
        }

        out.append(getActorName(QRBill.ACTOR_UDR) + "\n");
        out.append(getActorStreet(QRBill.ACTOR_UDR) + "\n");
        out.append(getActorHouseNumber(QRBill.ACTOR_UDR) + "\n");
        out.append(getActorPostcode(QRBill.ACTOR_UDR) + "\n");
        out.append(getActorLocation(QRBill.ACTOR_UDR) + "\n");
        out.append(getActorCountry(QRBill.ACTOR_UDR) + "\n");

        out.append(getReferenceType() + "\n");
        out.append(getReference() + "\n");
        out.append((getAdditionalInfo() != null ? getAdditionalInfo() : "") + "\n");

        String[] av = getAlternativeSchema(-1);
        out.append(av[0] + "\n");
        out.append(av[1]);

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
        String code = toString();
        String error = validateData (code, strict);
        if (error != null)
            throw new QRBillException(error);
        return code;
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
    public String getVersion () { return this.version; }

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
    public String getIBAN () { return this.konto; }

    /**
     * Gets the amount payable.
     *
     * @return Float. Returns the amount payable as a float, accurate to two decimal places. If a
     * value of -1 is returned then there is no amount payable registered.
     */
    public float getAmount () { return this.betrag; }

    /**
     * Gets the currency.
     *
     * @return String. Returns the currency of the amount payable. Possible values:
     * {@link #CURRENCY_CHF}, {@link #CURRENCY_EUR}
     */
    public String getCurrency () { return this.wahrung; }

    /**
     * Gets the reference type. Note that if this is {@link #REFTYPE_NON}, the value for reference
     * will be blank.
     *
     * @return String. Returns the reference type. Possible values: {@link #REFTYPE_QRR},
     * {@link #REFTYPE_SCOR}, {@link #REFTYPE_NON}.
     */
    public String getReferenceType () { return this.referenztyp; }

    /** Gets the bill reference. Note that if the value for reference type is {@link #REFTYPE_NON},
     * the value for this will be blank.
     *
     * @return tring. Returns the reference. This can be up to 27 characters long or blank.
     */
    public String getReference () { return this.referenz; }

    /**
     * Gets any additional information.
     *
     * @return String. The additional bill information.
     */
    public String getAdditionalInfo () { return this.zusInfo; }

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
     * Gets the street address of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor street address, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorStreet (int actorType) { return this.actors[actorType].strasse; }

    /**
     * Gets the house number of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor house number, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorHouseNumber (int actorType) { return this.actors[actorType].hausnummer; }

    /**
     * Gets the postcode of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor postcode, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorPostcode (int actorType) { return this.actors[actorType].postleitzahl; }

    /**
     * Gets the location (town, city, etc) of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor location, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorLocation (int actorType) { return this.actors[actorType].ort; }

    /**
     * Gets the country of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: {@link #ACTOR_CR},
     * {@link #ACTOR_UCR}, {@link #ACTOR_UDR}.
     *
     * @return String. The actor country, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    public String getActorCountry (int actorType) { return this.actors[actorType].land; }

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
            return this.av;
        } else {
            if (this.av[index] == null || this.av[index].length() < 3) {
                return null;
            } else {
                String token = this.av[index].substring(0, 2);
                String del = String.valueOf(this.av[index].charAt(2));
                String[] data = this.av[index].substring(3).split(del);
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
        if (this.zahlbarBis == null || this.zahlbarBis.length() == 0) {
            return null;
        } else {
            String[] tempDate = this.zahlbarBis.split("-");
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
     * Sets the QR Bill version number. Required for a valid QR Bill.
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
                int v = Integer.parseInt(version);
                if (v <= (int) QRBill.VERSION_SUPPORTED * 100) {
                    this.version = version;
                    return true;
                } else {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
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
        this.konto = validateStr(iban, true, 21);
        if (this.konto == null) {
            return false;
        } else {
            if (this.konto.toUpperCase().startsWith("CH")) {
                return true;
            } else if (this.konto.toUpperCase().startsWith("LI")) {
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
            this.betrag = -1;
            return true;
        } else {
            String amount = formatAmountAsString(amt);
            this.betrag = Float.parseFloat(amount);
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
                this.wahrung = currency.toUpperCase();
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
            this.zahlbarBis = year + "-"
                    + ("00" + String.valueOf(month)).substring(String.valueOf(month).length())
                    + "-" + ("00" + String.valueOf(day)).substring(String.valueOf(day).length());
        } else {
            this.zahlbarBis = "";
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
            this.referenztyp = refType.toUpperCase();

        if (ref != null)
            ref = ref.replace(" ","").trim();
        switch (refType) {
            case QRBill.REFTYPE_QRR:
                this.referenz = validateStr(ref, true, 27);
                // TODO May want to improve validation with recursive digit check after modular 10 (27th digit of the reference).
                if (this.referenz == null)
                    valid = false;
                break;
            case QRBill.REFTYPE_SCOR:
                this.referenz = validateStr(ref, true, 25);
                if (this.referenz == null)
                    valid = false;
                break;
            case QRBill.REFTYPE_NON:
                this.referenz = "";
                break;
            default:
                valid = false;
        }

        return valid;
    }

    /**
     * Sets the additional information field.
     *
     * @param info String. The text of the additional information. Maximum length of 140 characters.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    public boolean setAdditionalInfo(String info) {
        this.zusInfo = validateStr(info, false, 140);
        return true;
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
     * </ui>
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
            this.av[index] = "";
        } else {
            this.av[index] = validateStr(data, false, 100);
        }
        return true;
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
     * </ui>
     *
     * @param data data String Array. The alternative schema data as an array. Maximum length of each entry
     *            is 100 characters, no maximum length is specified for the second. If the array only contains
     *             one element then this is assigned as the first and the second is cleared. If the array
     *             contains more than two elements then all elements after the second are ignored. If null all
     *             entries are cleared.
     *
     * @return Boolean. Always returns true as, even if not set, the field is not mandatory.
     */
    public boolean setAlternativeSchema(String[] data) {
        if (data == null) {
            this.av[0] = "";
            this.av[1] = "";
        } else for (int i = 0; i < data.length; i++) {
            setAlternativeSchema(data[i], i);
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
        if (typeId != QRBill.ACTOR_CR && typeId != QRBill.ACTOR_UCR && typeId != QRBill.ACTOR_UDR) {
            return false;
        } else {
            this.actors[typeId].name = validateStr(name, true, 70);
            this.actors[typeId].strasse = validateStr(street, false, 70);
            this.actors[typeId].hausnummer = validateStr(housenumber, false, 16);
            this.actors[typeId].postleitzahl = validateStr(postalcode, true, 16);
            this.actors[typeId].ort = validateStr(location, true, 35);
            this.actors[typeId].land = validateStr(country, true, 2);

            return validateDependancies(typeId, this.strict);
        }
    }

    private String validateData (String rawData, boolean strict) {
        if (rawData == null || rawData.length() == 0)
            return "Input data empty or null.";

        if (rawData.length() > 997)
            return "Input data exceeds maximum allowed limit.";

        String[] qrData = rawData.trim().split("\n");
        if (qrData.length < 25) {
            return "Malformed Data - insufficient fields.";
        } else if (!setVersion(qrData[1]) && strict) {
            return "Version invalid or not supported";
        }

        this.actors[0] = new Actor(QRBill.ACTOR_CR);
        this.actors[1] = new Actor(QRBill.ACTOR_UCR);
        this.actors[2] = new Actor(QRBill.ACTOR_UDR);
        String refType = QRBill.REFTYPE_NON;
        int[] actorIds;

        for (int i = 0; i < qrData.length; i++) {
            String item = qrData[i];
            switch (i) {
                case 0: // QRType
                    if (!setQrType(item) && strict)
                        return "QR Type invalid or not supported";
                    break;
                case 1: // Version
                    // Ignore - already validated above
                    break;
                case 2: // Coding Type
                    if (!setCodingType(item) && strict)
                        return "Valid Coding type Missing";
                    break;
                case 3: // Konto
                    if (!setIBAN(item) && strict)
                        return "Valid IBAN Missing";
                    break;
                case 4: // ZE – Name
                case 10: // EZE – Name
                case 19: // EZP – Name
                    actorIds = new int[] {4, 10, 19};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == i) {
                            this.actors[j].name = validateStr(item, true, 70);
                        }
                    }
                    break;
                case 5: // ZE – Strasse
                case 11: // EZE – Strasse
                case 20: // EZP – Strasse
                    actorIds = new int[] {5, 11, 20};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == i) {
                            this.actors[j].strasse = validateStr(item, false, 70);
                        }
                    }
                    break;
                case 6: // ZE – Hausnummer
                case 12: // EZE – Hausnummer
                case 21: // EZP – Hausnummer
                    actorIds = new int[] {6, 12, 21};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == i) {
                            this.actors[j].hausnummer = validateStr(item, false, 16);
                        }
                    }
                    break;
                case 7: // ZE – Postleitzahl
                case 13: // EZE – Postleitzahl
                case 22: // EZP – Postleitzahl
                    actorIds = new int[] {7, 13, 22};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == i) {
                            this.actors[j].postleitzahl = validateStr(item, true, 16);
                        }
                    }
                    break;
                case 8: // ZE – Ort
                case 14: // EZE – Ort
                case 23: // EZP – Ort
                    actorIds = new int[] {8, 14, 23};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == i) {
                            this.actors[j].ort = validateStr(item, true, 35);
                        }
                    }
                    break;
                case 9: // ZE – Land
                case 15: // EZE – Land
                case 24: // EZP – Land
                    actorIds = new int[] {9, 15, 24};
                    for (int j = 0; j < actorIds.length; j++) {
                        if (actorIds[j] == i) {
                            this.actors[j].land = validateStr(item.toUpperCase(), true, 2);
                        }
                    }
                    break;
                case 16: // Betrag
                    setAmount(item);
                    break;
                case 17: // Währung
                    if (!setCurrency(item) && strict)
                        return "Valid Currency Missing";
                    break;
                case 18: // Zahlbar bis
                    setDueDate(item);
                    break;
                case 25: // Referenztyp
                    refType = item;
                    break;
                case 26: // Referenz
                    if (!setReference(refType, item) && strict)
                        return "Valid Reference Missing";
                    break;
                case 27: // Zusätzliche Informationen
                    setAdditionalInfo(item);
                    break;
                case 28: // AV1 – Parameter
                    setAlternativeSchema(item, 0);
                    break;
                case 29: // AV2 – Parameter
                    setAlternativeSchema(item, 1);
                    break;
            }
        }

        if (getReference() == null)
            setReference(QRBill.REFTYPE_NON, "");

        boolean allGood = true;
        for (int i = 0; i < this.actors.length; i++)
            if (!validateDependancies(i, strict))
                allGood = false;

        if (!allGood && strict)
            return "Mandatory actor dependancies not met.";

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

    private boolean validateDependancies (int typeId, boolean strict) {
        if (typeId > 2 || typeId < 0)
            return false;

        StringBuffer test = new StringBuffer();
        if (this.actors[typeId].name != null) test.append(this.actors[typeId].name.trim());
        if (this.actors[typeId].strasse != null) test.append(this.actors[typeId].strasse.trim());
        if (this.actors[typeId].hausnummer != null) test.append(this.actors[typeId].hausnummer.trim());
        if (this.actors[typeId].postleitzahl != null) test.append(this.actors[typeId].postleitzahl.trim());
        if (this.actors[typeId].ort != null) test.append(this.actors[typeId].ort.trim());
        if (this.actors[typeId].land != null) test.append(this.actors[typeId].land.trim());
        boolean hasEntry = test.length() > 0;
        boolean valid = true;

        if (hasEntry) {
            if (this.actors[typeId].name == null || this.actors[typeId].name.length() == 0)
                valid = false;
            if (this.actors[typeId].postleitzahl == null || this.actors[typeId].postleitzahl.length() == 0)
                valid = false;
            if (this.actors[typeId].ort == null || this.actors[typeId].ort.length() == 0)
                valid = false;
            if (this.actors[typeId].land == null || this.actors[typeId].land.length() == 0)
                valid = false;
        } else if (typeId == QRBill.ACTOR_CR) {
            valid = false;
        }

        if (valid && typeId > 0) {
            if (this.actors[typeId].name == null)
                this.actors[typeId].name = "";
            if (this.actors[typeId].strasse == null)
                this.actors[typeId].strasse = "";
            if (this.actors[typeId].hausnummer == null)
                this.actors[typeId].hausnummer = "";
            if (this.actors[typeId].postleitzahl == null)
                this.actors[typeId].postleitzahl = "";
            if (this.actors[typeId].ort == null)
                this.actors[typeId].ort = "";
            if (this.actors[typeId].land == null)
                this.actors[typeId].land = "";
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

    /**
     * Exception thrown upon validation failure whenever the class is instantiated with raw QR code
     * or {@link #getQRCode} is called.
     */
    public class QRBillException extends Exception {
        public QRBillException(String msg) {
            super(msg);
        }
    }

    private class Actor {
        public String name = "";
        public String strasse = "";
        public String hausnummer = "";
        public String postleitzahl = "";
        public String ort = "";
        public String land = "";

        private int typeId;

        public Actor (int type) {
            this.typeId = type;
        }

        public int getType () {
            return this.typeId;
        }
    }

}
