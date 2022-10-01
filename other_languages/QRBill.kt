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
package com.gfb.test

import com.gfb.test.QRBill.Actor
import com.gfb.test.QRBill.Modulo10

/**
 * <h1>Swiss Payments Code Serializer</h1>
 *
 *
 * This class was designed to be a bare bones serializer for the Swiss Payments Code, which is
 * meant to adapt the payment system in Switzerland and Liechtenstein to the international ISO 20022
 * standard. For more information on this please consult the
 * [PaymentStandards.ch](http://www.paymentstandards.ch/) site, as the
 * [implementation
 * guide](https://www.paymentstandards.ch/dam/downloads/ig-qr-bill-en.pdf) here was the basis for this class.
 *
 *
 * This class was purposely written using only core Java and without any imports, so that it can
 * more easily adapted to other languages and platforms. As such it does not implement
 * [java.io.Serializable], out of the box, but will do so if simply added. It is designed, not
 * only to serialize, but also to validate data according to the above standard.
 *
 * @author  Gaddo F Benedetti
 * @version 2.3.1
 * @since   2019-11-23
 */
class QRBill {
    /**
     * Gets the QR Type Identifier used.
     *
     * @return String. The QR Type Identifier. Possible values: [.QRTYPE_SPC]
     */
    // TODO Remove the use of the strict param, as it serves little practical purpose.
    var qrType: String? = null
        private set

    /**
     * Gets the QR Bill version number.
     *
     * @return String. The QR Bill version number as a 4-character numerical string, with leading
     * zeros. The first two represent the major version, the second two represent the minor
     * version (e.g. "0100" corresponds to version 1.0).
     */
    var version: Float? = null
        private set

    /**
     * Gets the Character Set used.
     *
     * @return String. The Character Set. Possible values: [.CODING_LATIN_1]
     */
    var codingType = 0
        private set

    /**
     * Gets the IBAN used.
     *
     * @return String. The IBAN.
     */
    var iBAN: String? = null
        private set

    /**
     * Gets the amount payable.
     *
     * @return Float. Returns the amount payable as a float, accurate to two decimal places. If a
     * value of -1 is returned then there is no amount payable registered.
     */
    var amount = 0f
        private set

    /**
     * Gets the currency.
     *
     * @return String. Returns the currency of the amount payable. Possible values:
     * [.CURRENCY_CHF], [.CURRENCY_EUR]
     */
    var currency: String? = null
        private set
    private var dueDate: String? = null

    /**
     * Gets the reference type. Note that if this is [.REFTYPE_NON], the value for reference
     * will be blank.
     *
     * @return String. Returns the reference type. Possible values: [.REFTYPE_QRR],
     * [.REFTYPE_SCOR], [.REFTYPE_NON].
     */
    var referenceType: String? = null
        private set

    /** Gets the bill reference. Note that if the value for reference type is [.REFTYPE_NON],
     * the value for this will be blank.
     *
     * @return tring. Returns the reference. This can be up to 27 characters long or blank.
     */
    var reference: String? = null
        private set

    /**
     * Gets any unstructured message included.
     *
     * @return String. The unstructured message.
     */
    var unstructuredMsg: String? = ""
        private set

    /**
     * Gets the Trailer, which is the unambiguous indicator for the end of payment data.
     *
     * @return String. Possible values: [.TRAILER_EPD].
     */
    var trailer: String? = null
        private set

    /**
     * Gets the bill information which can be used as coded information for automated
     * booking of the payment.
     *
     * @return String. The bill information.
     */
    var billInfo: String? = ""
        private set
    private val `as` = arrayOf<String?>("", "")
    private val actors = arrayOf(
        Actor(ACTOR_CR),
        Actor(ACTOR_UCR),
        Actor(ACTOR_UDR)
    )

    private enum class Data {
        NONE, QRTYPE, VERSION, CODING, ACCOUNT, AMOUNT, CURRENCY, DUEDATE, REF_TYPE, REF, CR_ADDTYPE, CR_NAME, CR_ADDRESS1, CR_ADDRESS2, CR_POSTCODE, CR_LOCATION, CR_COUNTRY, UCR_ADDTYPE, UCR_NAME, UCR_ADDRESS1, UCR_ADDRESS2, UCR_POSTCODE, UCR_LOCATION, UCR_COUNTRY, UDR_ADDTYPE, UDR_NAME, UDR_ADDRESS1, UDR_ADDRESS2, UDR_POSTCODE, UDR_LOCATION, UDR_COUNTRY, UNSTR_MSG, TRAILER, BILLINFO, ALTSCHEMA1, ALTSCHEMA2
    }

    private val version1 = arrayOf(
        Data.QRTYPE, Data.VERSION, Data.CODING, Data.ACCOUNT,
        Data.CR_NAME, Data.CR_ADDRESS1, Data.CR_ADDRESS2, Data.CR_POSTCODE, Data.CR_LOCATION, Data.CR_COUNTRY,
        Data.UCR_NAME, Data.UCR_ADDRESS1, Data.UCR_ADDRESS2, Data.UCR_POSTCODE, Data.UCR_LOCATION, Data.UCR_COUNTRY,
        Data.AMOUNT, Data.CURRENCY, Data.DUEDATE,
        Data.UDR_NAME, Data.UDR_ADDRESS1, Data.UDR_ADDRESS2, Data.UDR_POSTCODE, Data.UDR_LOCATION, Data.UDR_COUNTRY,
        Data.REF_TYPE, Data.REF, Data.UNSTR_MSG, Data.ALTSCHEMA1, Data.ALTSCHEMA2
    )
    private val version2 = arrayOf(
        Data.QRTYPE,
        Data.VERSION,
        Data.CODING,
        Data.ACCOUNT,
        Data.CR_ADDTYPE,
        Data.CR_NAME,
        Data.CR_ADDRESS1,
        Data.CR_ADDRESS2,
        Data.CR_POSTCODE,
        Data.CR_LOCATION,
        Data.CR_COUNTRY,
        Data.UCR_ADDTYPE,
        Data.UCR_NAME,
        Data.UCR_ADDRESS1,
        Data.UCR_ADDRESS2,
        Data.UCR_POSTCODE,
        Data.UCR_LOCATION,
        Data.UCR_COUNTRY,
        Data.AMOUNT,
        Data.CURRENCY,
        Data.UDR_ADDTYPE,
        Data.UDR_NAME,
        Data.UDR_ADDRESS1,
        Data.UDR_ADDRESS2,
        Data.UDR_POSTCODE,
        Data.UDR_LOCATION,
        Data.UDR_COUNTRY,
        Data.REF_TYPE,
        Data.REF,
        Data.UNSTR_MSG,
        Data.TRAILER,
        Data.BILLINFO,
        Data.ALTSCHEMA1,
        Data.ALTSCHEMA2
    )

    /**
     * Constructor that generates a partially empty QR Billing object, with a number of default
     * values. These values are:
     *
     *  * QR Type Identifier: [.QRTYPE_SPC]
     *  * Version: {@value #VERSION_SUPPORTED}
     *  * Character Set: [.CODING_LATIN_1]
     *  * Reference Type: [.REFTYPE_NON]
     *  * Amount: None
     *  * Currency: [.CURRENCY_CHF]
     *  * Trailer: [.TRAILER_EPD]
     *
     *
     * Please note that this constructor and the use of the strict paramater is deprecated and
     * will be removed in the future.
     */
    @Deprecated("")
    constructor() {
        setQrType(QRTYPE_SPC)
        setVersion(VERSION_SUPPORTED)
        setCodingType(CODING_LATIN_1)
        setReference()
        setAmount()
        setCurrency(CURRENCY_CHF)
        setTrailer(TRAILER_EPD)
    }

    /**
     * Constructor that generates a QR Billing object, using raw QR Bill data as input.
     *
     * Please note that this constructor and the use of the strict paramater is deprecated and
     * will be removed in the future.
     *
     * @param rawData String. Basic QR Bill data. Fields should be separated by new lines. An
     * implementation guide on the format may be found at
     * http://www.paymentstandards.ch/
     */
    @Deprecated("")
    constructor(rawData: String?) {
        validateData(rawData)
    }

    /**
     * Returns whether the current QR Bill is valid.
     *
     * @return Boolean. Whether the current QR Bill is valid or not.
     */
    val isValid: Boolean
        get() = validateData(toString()).size == 0

    /**
     * Returns the Unvalidated QR Code in the QR Billing object.
     *
     * @return String. The unvalidated QR Bill data. Fields will be separated by new lines. An
     * implementation guide on the format may be found at http://www.paymentstandards.ch/
     */
    override fun toString(): String {
        val structure = structure
        val out = StringBuffer()
        if (structure != null) {
            for (element in structure) {
                when (element) {
                    Data.QRTYPE -> out.append(
                        """
                        $qrType
                        
                        """.trimIndent()
                    )
                    Data.VERSION -> out.append(
                        """
                            $formattedVersion
                            
                            """.trimIndent()
                    )
                    Data.CODING -> out.append(
                        """
                            $codingType
                            
                            """.trimIndent()
                    )
                    Data.ACCOUNT -> out.append(
                        """
                            $iBAN
                            
                            """.trimIndent()
                    )
                    Data.AMOUNT -> {
                        if (amount > 0) out.append(formatAmountAsString(amount))
                        out.append("\n")
                    }
                    Data.CURRENCY -> out.append(
                        """
                            $currency
                            
                            """.trimIndent()
                    )
                    Data.DUEDATE -> {
                        val dueDate = getDueDate()
                        if (dueDate == null) {
                            out.append("\n")
                        } else {
                            out.append(
                                """
                                    ${dueDate[0]}-${dueDate[1]}-${dueDate[2]}
                                    
                                    """.trimIndent()
                            )
                        }
                    }
                    Data.REF_TYPE -> out.append(
                        """
                            $referenceType
                            
                            """.trimIndent()
                    )
                    Data.REF -> out.append(
                        """
                            $reference
                            
                            """.trimIndent()
                    )
                    Data.CR_ADDTYPE -> out.append(
                        """
                            ${getActorAddressType(ACTOR_CR)}
                            
                            """.trimIndent()
                    )
                    Data.CR_NAME -> out.append(
                        """
                            ${getActorName(ACTOR_CR)}
                            
                            """.trimIndent()
                    )
                    Data.CR_ADDRESS1 -> out.append(
                        """
                            ${getActorStreet(ACTOR_CR)}
                            
                            """.trimIndent()
                    )
                    Data.CR_ADDRESS2 -> out.append(
                        """
                            ${getActorHouseNumber(ACTOR_CR)}
                            
                            """.trimIndent()
                    )
                    Data.CR_POSTCODE -> out.append(
                        """
                            ${getActorPostcode(ACTOR_CR)}
                            
                            """.trimIndent()
                    )
                    Data.CR_LOCATION -> out.append(
                        """
                            ${getActorLocation(ACTOR_CR)}
                            
                            """.trimIndent()
                    )
                    Data.CR_COUNTRY -> out.append(
                        """
                            ${getActorCountry(ACTOR_CR)}
                            
                            """.trimIndent()
                    )
                    Data.UCR_ADDTYPE -> out.append(
                        """
                            ${getActorAddressType(ACTOR_UCR)}
                            
                            """.trimIndent()
                    )
                    Data.UCR_NAME -> out.append(
                        """
                            ${getActorName(ACTOR_UCR)}
                            
                            """.trimIndent()
                    )
                    Data.UCR_ADDRESS1 -> out.append(
                        """
                            ${getActorStreet(ACTOR_UCR)}
                            
                            """.trimIndent()
                    )
                    Data.UCR_ADDRESS2 -> out.append(
                        """
                            ${getActorHouseNumber(ACTOR_UCR)}
                            
                            """.trimIndent()
                    )
                    Data.UCR_POSTCODE -> out.append(
                        """
                            ${getActorPostcode(ACTOR_UCR)}
                            
                            """.trimIndent()
                    )
                    Data.UCR_LOCATION -> out.append(
                        """
                            ${getActorLocation(ACTOR_UCR)}
                            
                            """.trimIndent()
                    )
                    Data.UCR_COUNTRY -> out.append(
                        """
                            ${getActorCountry(ACTOR_UCR)}
                            
                            """.trimIndent()
                    )
                    Data.UDR_ADDTYPE -> out.append(
                        """
                            ${getActorAddressType(ACTOR_UDR)}
                            
                            """.trimIndent()
                    )
                    Data.UDR_NAME -> out.append(
                        """
                            ${getActorName(ACTOR_UDR)}
                            
                            """.trimIndent()
                    )
                    Data.UDR_ADDRESS1 -> out.append(
                        """
                            ${getActorStreet(ACTOR_UDR)}
                            
                            """.trimIndent()
                    )
                    Data.UDR_ADDRESS2 -> out.append(
                        """
                            ${getActorHouseNumber(ACTOR_UDR)}
                            
                            """.trimIndent()
                    )
                    Data.UDR_POSTCODE -> out.append(
                        """
                            ${getActorPostcode(ACTOR_UDR)}
                            
                            """.trimIndent()
                    )
                    Data.UDR_LOCATION -> out.append(
                        """
                            ${getActorLocation(ACTOR_UDR)}
                            
                            """.trimIndent()
                    )
                    Data.UDR_COUNTRY -> out.append(
                        """
                            ${getActorCountry(ACTOR_UDR)}
                            
                            """.trimIndent()
                    )
                    Data.UNSTR_MSG -> out.append(
                        """
                            ${if (unstructuredMsg != null) unstructuredMsg else ""}
                            
                            """.trimIndent()
                    )
                    Data.ALTSCHEMA1 -> {
                        val as1 = getAlternativeSchema(-1)
                        out.append(
                            """
                                ${as1!![0]}
                                
                                """.trimIndent()
                        )
                    }
                    Data.ALTSCHEMA2 -> {
                        val as2 = getAlternativeSchema(-1)
                        out.append(
                            """
                                ${as2!![1]}
                                
                                """.trimIndent()
                        )
                    }
                    Data.TRAILER -> out.append(
                        """
                            $trailer
                            
                            """.trimIndent()
                    )
                    Data.BILLINFO -> out.append(
                        """
                            $billInfo
                            
                            """.trimIndent()
                    )
                }
            }
        }
        return out.toString().trim { it <= ' ' }
    }

    /**
     * Gets the QR Code in the QR Billing object.
     *
     * @return String. The validated QR Bill data. Fields will be separated by new lines. An
     * implementation guide on the format may be found at http://www.paymentstandards.ch/
     * @throws QRBillException Thrown when validation fails. Exception message gives a
     * description of the validation error.
     */
    @get:kotlin.Throws(QRBillException::class)
    val qRCode: String
        get() = toString()
    val formattedVersion: String
        get() {
            var fv = (version!! * 100).toInt().toString()
            if (fv.length < 4) fv = "0$fv"
            return fv
        }

    /**
     * Gets any additional information. Deprecated - use getUnstructuredMsg instead.
     *
     * This method is a copy of the #getUnstructuredMsg method, which uses the same termenology
     * used from version 2.0 of the specification. As such it is deprecated and will be removed
     * in the future.
     *
     * @return String. The additional bill information.
     */
    @get:Deprecated("")
    val additionalInfo: String?
        get() = unstructuredMsg

    /**
     * Gets the name of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: [.ACTOR_CR],
     * [.ACTOR_UCR], [.ACTOR_UDR].
     *
     * @return String. The actor name, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    fun getActorName(actorType: Int): String? {
        return actors[actorType].name
    }

    /**
     * Gets the address type of the specified actor.
     *
     * @param actorType actorType Integer. The actor type being queried. Possible values:
     * [.ACTOR_CR], [.ACTOR_UCR], [.ACTOR_UDR].
     *
     * @return String. The actor address type. Possible values: [.ADDTYPE_STRUCTURED],
     * [.ADDTYPE_COMBINED].
     */
    fun getActorAddressType(actorType: Int): String? {
        return actors[actorType].addressType
    }

    /**
     * Gets the street address of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: [.ACTOR_CR],
     * [.ACTOR_UCR], [.ACTOR_UDR].
     *
     * @return String. The actor street address, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    fun getActorStreet(actorType: Int): String? {
        return actors[actorType].address1
    }

    /**
     * Gets the house number of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: [.ACTOR_CR],
     * [.ACTOR_UCR], [.ACTOR_UDR].
     *
     * @return String. The actor house number, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    fun getActorHouseNumber(actorType: Int): String? {
        return actors[actorType].address2
    }

    /**
     * Gets the postcode of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: [.ACTOR_CR],
     * [.ACTOR_UCR], [.ACTOR_UDR].
     *
     * @return String. The actor postcode, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    fun getActorPostcode(actorType: Int): String? {
        return actors[actorType].postcode
    }

    /**
     * Gets the location (town, city, etc) of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: [.ACTOR_CR],
     * [.ACTOR_UCR], [.ACTOR_UDR].
     *
     * @return String. The actor location, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    fun getActorLocation(actorType: Int): String? {
        return actors[actorType].location
    }

    /**
     * Gets the country of the specified actor.
     *
     * @param actorType Integer. The actor type being queried. Possible values: [.ACTOR_CR],
     * [.ACTOR_UCR], [.ACTOR_UDR].
     *
     * @return String. The actor country, as a string, blank if not mandatory and null when
     * mandatory, but omitted.
     */
    fun getActorCountry(actorType: Int): String? {
        return actors[actorType].country
    }

    /**
     * Gets all Alternative Schemas.
     *
     * @return String Array. If unprocessed, a string array of up to two String elements, denoting
     * the two lines as presented in the QR code will be returned.
     */
    val alternativeSchema: Array<String?>?
        get() = getAlternativeSchema(-1)

    /**
     * Gets a specified Alternative Schema.
     *
     * @param index Integer. The index being entered - either 0 or 1. Any other value will return all
     * schemas unprocessed (see below) as a two element String array.
     *
     * @return String Array. If processed a string array  beginning with the two-character schema
     * identifier as the first element, the single-character deliminator as the second and
     * subsequent elements will be the individual schema data, processed using the deliminator. If
     * unprocessed, a string array of up to two String elements, denoting the two lines as presented
     * in the QR code will be returned.
     */
    fun getAlternativeSchema(index: Int): Array<String?>? {
        return if (index > 1 || index < 0) {
            `as`
        } else {
            if (`as`[index] == null || `as`[index]!!.length < 3) {
                null
            } else {
                val token: String = `as`[index].substring(0, 2)
                val del = `as`[index]!![2].toString()
                val data: Array<String> = `as`[index].substring(3).split(del).toTypedArray()
                val retArray = arrayOfNulls<String?>(data.size + 2)
                retArray[0] = token
                retArray[1] = del
                for (i in 2 until retArray.size) retArray[i] = data[i - 2]
                retArray
            }
        }
    }

    /**
     * Gets the due date, if available.
     *
     * @return Integer Array. Three-element array, giving the year, month and day, in that order. If
     * no value is available, null is returned.
     */
    fun getDueDate(): IntArray? {
        return if (dueDate == null || dueDate!!.length == 0) {
            null
        } else {
            val tempDate: Array<String> = dueDate.split("-").toTypedArray()
            if (tempDate.size != 3) {
                null
            } else {
                try {
                    val year: Int = tempDate[0].toInt()
                    val month: Int = tempDate[1].toInt()
                    val day: Int = tempDate[2].toInt()
                    intArrayOf(year, month, day)
                } catch (e: NumberFormatException) {
                    null
                }
            }
        }
    }

    /**
     * Sets the QR Type Identifier used. Required for a valid QR Bill.
     *
     * @param qrType String. The QR Type Identifier. Possible values: [.QRTYPE_SPC]
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setQrType(qrType: String?): Boolean {
        return if (qrType != null && qrType.equals(QRTYPE_SPC, ignoreCase = true)) {
            this.qrType = qrType.uppercase(Locale.getDefault())
            true
        } else {
            false
        }
    }

    /**
     * Sets the QR Bill version number directly from an input of a 4-character numerical string, as
     * described in the specification. Required for a valid QR Bill.
     *
     * @param version String. The QR Bill version number as a 4-character numerical string, with
     * leading zeros. The first two represent the major version, the second two
     * represent the minor version (e.g. "0100" corresponds to version 1.0).
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setVersion(version: String?): Boolean {
        return if (version == null || version.length != 4) {
            false
        } else {
            try {
                val v: Float = version.toFloat() / 100
                setVersion(v)
            } catch (e: NumberFormatException) {
                false
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
    fun setVersion(version: Float): Boolean {
        return if (version <= VERSION_SUPPORTED) {
            this.version = version
            true
        } else {
            false
        }
    }

    /**
     * Sets the Character Set used. Required for a valid QR Bill.
     *
     * @param codingType String. The Character Set. Possible values: [.CODING_LATIN_1]
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setCodingType(codingType: Int): Boolean {
        val codingTypes = intArrayOf(CODING_LATIN_1)
        var valid = false
        for (code in codingTypes) {
            if (codingType == code) valid = true
        }
        return if (valid) {
            this.codingType = codingType
            true
        } else {
            false
        }
    }

    /**
     * Sets the IBAN used. Required for a valid QR Bill.
     *
     * @param iban String. A valid IBAN. Maximum length of 21 characters.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setIBAN(iban: String): Boolean {
        var iban = iban
        iban = iban.replace(" ", "").trim { it <= ' ' }
        for (i in 0 until iban.length) if ("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(
                iban.uppercase(Locale.getDefault()).get(i)
            ) == -1
        ) return false
        iBAN = validateStr(iban, true, 21)
        return if (iBAN == null) {
            false
        } else {
            if (iBAN.uppercase(Locale.getDefault()).startsWith("CH")) {
                true
            } else if (iBAN.uppercase(Locale.getDefault()).startsWith("LI")) {
                true
            } else {
                false
            }
        }
    }

    /**
     * Sets the amount payable to empty - the same as setting it to a value of -1.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setAmount(): Boolean {
        return setAmount(-1f)
    }

    /**
     * Sets the amount payable.
     *
     * @param amt Float. The amount payable as a float, accurate to two decimal places. If a value
     * of -1 is used then the amount payable will be set as empty.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setAmount(amt: Float): Boolean {
        return if (amt < 0) {
            amount = -1f
            true
        } else {
            val amount = formatAmountAsString(amt)
            this.amount = amount.toFloat()
            if (amount.length > 12) {
                false
            } else {
                true
            }
        }
    }

    /**
     * Sets the currency. Required for a valid QR Bill.
     *
     * @param currency String. The currency of the amount payable. Possible values:
     * [.CURRENCY_CHF], [.CURRENCY_EUR]
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setCurrency(currency: String?): Boolean {
        return if (currency == null || currency.length == 0) {
            false
        } else {
            val currencies = arrayOf(CURRENCY_CHF, CURRENCY_EUR)
            var valid = false
            for (cur in currencies) {
                if (currency.equals(cur, ignoreCase = true)) valid = true
            }
            if (valid) this.currency = currency.uppercase(Locale.getDefault())
            valid
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
    fun setDueDate(year: Int, month: Int, day: Int): Boolean {
        var isValid = true
        if (year < 2018 || year > 9999) isValid = false
        if (month < 1 || month > 12) isValid = false
        val maxDays = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        if (day < 1 || day > maxDays[month - 1]) isValid = false
        if (month == 2 && day == 29) {
            if (year % 4 > 0) isValid = false
            if (year % 100 == 0 && year % 400 > 0) isValid = false
        }
        if (isValid) {
            dueDate = (year.toString() + "-"
                    + "00$month".substring(month.toString().length)
                    + "-" + "00$day".substring(day.toString().length))
        } else {
            dueDate = ""
        }
        return true
    }

    /**
     * Sets the reference type as [.REFTYPE_NON], wuth an empty reference.
     *
     * @return Always returns true.
     */
    fun setReference(): Boolean {
        return setReference(REFTYPE_NON, null)
    }

    /**
     * Sets the reference type and reference itslef.
     *
     * @param refType String. The currency of the amount payable. Possible values:
     * [.REFTYPE_QRR], [.REFTYPE_SCOR], [.REFTYPE_NON]
     * @param ref String. The reference. In the case of [.REFTYPE_QRR] and
     * [.REFTYPE_SCOR], this must represent valid QRR and SCOR references, of maximum
     * 27 annd 25 characters lengths respectively. For [.REFTYPE_NON], this
     * value will be ignored and the reference left empty.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setReference(refType: String?, ref: String?): Boolean {
        var ref = ref
        var valid = false
        val refTypes = arrayOf(REFTYPE_QRR, REFTYPE_SCOR, REFTYPE_NON)
        for (type in refTypes) {
            if (refType != null && refType.equals(type, ignoreCase = true)) valid = true
        }
        if (valid) referenceType = refType.uppercase(Locale.getDefault())
        if (ref != null) ref = ref.replace(" ", "").trim { it <= ' ' }
        when (refType) {
            REFTYPE_QRR -> {
                reference = validateStr(ref, true, 27)
                if (reference == null || !Modulo10.validate(reference)) valid = false
            }
            REFTYPE_SCOR -> {
                reference = validateStr(ref, true, 25)
                if (reference == null) valid = false
            }
            REFTYPE_NON -> reference = ""
            else -> valid = false
        }
        return valid
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
    @Deprecated("")
    fun setAdditionalInfo(info: String): Boolean {
        return setUnstructuredMsg(info)
    }

    /**
     * Sets an unstructured message.
     *
     * @param unstructuredMsg String. The text of the unstructured message (version 2.0+, formerly
     * known as additional info in version 1.0). Maximum length of 140 characters.
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setUnstructuredMsg(unstructuredMsg: String): Boolean {
        this.unstructuredMsg = validateStr(unstructuredMsg, false, 140)
        return unstructuredMsg.length > 140
    }

    /**
     * Unambiguous indicator for the end of payment data. Fixed value "EPD" (End Payment Data), so
     * the method realistically does not allow anything other than that value at present.
     *
     * @param trailer String. Only poaaible value, currently, is [.TRAILER_EPD].
     *
     * @return Whether the value has validated and stored correctly or not.
     */
    fun setTrailer(trailer: String?): Boolean {
        var trailer = trailer ?: return false
        trailer = trailer.uppercase(Locale.getDefault()).trim { it <= ' ' }
        return if (trailer.length != 3) false else when (trailer) {
            TRAILER_EPD -> {
                this.trailer = trailer
                true
            }
            else -> false
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
    fun setBillInfo(billInfo: String): Boolean {
        this.billInfo = validateStr(billInfo, false, 140)
        return billInfo.length > 140
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
     *
     *  *
     * Up to two alternate schemas may exist, each taking up a single line in the QR code.
     * An alternate schema entry is not mandatory.
     *
     *  *
     * Each alternate schemas entry may be up to 100 characters in length.
     *
     *
     *
     * @param data String. The alternative schema data as two entries. Maximum length of each entry
     * is 100 characters, no maximum length is specified for the second. If null the entry
     * is cleared.
     * @param index Integer. The index being entered - either 0 or 1.
     *
     * @return Boolean. Always returns true as, even if not set, the field is not mandatory.
     */
    fun setAlternativeSchema(data: String?, index: Int): Boolean {
        return if (data == null || index < 0 || index > 1) {
            false
        } else {
            `as`[index] = validateStr(data, false, 100)
            true
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
     *
     *  *
     * Up to two alternate schemas may exist, each taking up a single line in the QR code.
     * An alternate schema entry is not mandatory.
     *
     *  *
     * Each alternate schemas entry may be up to 100 characters in length.
     *
     *
     *
     * @param data String Array. The alternative schema data as an array. Maximum length of each entry
     * is 100 characters, no maximum length is specified for the second. If the array only contains
     * one element then this is assigned as the first and the second is cleared. If the array
     * contains more than two elements then all elements after the second are ignored. If null all
     * entries are cleared.
     *
     * @return Boolean. Always returns true as, even if not set, the field is not mandatory.
     */
    fun setAlternativeSchema(data: Array<String?>?): Boolean {
        if (data == null) {
            `as`[0] = ""
            `as`[1] = ""
        } else for (i in data.indices) {
            if (!setAlternativeSchema(data[i], i)) return false
        }
        return true
    }

    /**
     * Clears the Alternative Schema entries.
     *
     * @return Boolean. Always returns true as, even if not set, the field is not mandatory.
     */
    fun setAlternativeSchema(): Boolean {
        return setAlternativeSchema(null)
    }

    /**
     * Sets an actor associated with the QR Bill. An actor is one of several legal entities affected
     * by the QR Bill, namely the creditor, ultimate creditor or ultimate debtor.
     *
     * This method is complient with the version 1.0 of the specification.
     *
     * @param typeId Integer. The actor type. Possible values:
     * [.ACTOR_CR], [.ACTOR_UCR], [.ACTOR_UDR]
     * @param name String. Mandatory. Maximum length of 70 characters. The actor full name.
     * @param street String. Optional. Maximum length of 70 characters. The actor street address.
     * @param housenumber String. Optional. Maximum length of 16 characters. The actor house number.
     * @param postalcode String. Optional. Maximum length of 16 characters. The actor post code.
     * @param location String. Mandatory. Maximum length of 35 characters. The actor location,
     * such as town or city.
     * @param country  String. Mandatory. Maximum length of 2 characters. The actor country, given
     * as a 2-letter country code (ISO 3166-1).
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setActor(
        typeId: Int,
        name: String?,
        street: String?,
        housenumber: String?,
        postalcode: String?,
        location: String?,
        country: String?
    ): Boolean {
        return setActor(typeId, name, null, street, housenumber, postalcode, location, country)
    }

    /**
     * Sets an actor associated with the QR Bill. An actor is one of several legal entities affected
     * by the QR Bill, namely the creditor, ultimate creditor or ultimate debtor.
     *
     * This method is complient with the versions 1.0 to 2.0 of the specification.
     *
     * @param typeId Integer. The actor type. Possible values:
     * [.ACTOR_CR], [.ACTOR_UCR], [.ACTOR_UDR]
     * @param name String. Mandatory. Maximum length of 70 characters. The actor full name.
     * @param addressType String. Mandatory. The format used for the actor address.
     * @param address1 String. Dependant. Maximum length of 70 characters. The optional actor street address
     * or mandatory actor street address and house number, depending on version and Address Type.
     * @param address2 String. Dependant. Maximum length of 16 characters. The actor optional house
     * number ot mandatory actor post code and location, depending on version and Address Type.
     * @param postalcode String. Optional. Maximum length of 16 characters. The actor post code.
     * @param location String. Dependant. Maximum length of 35 characters. The actor location,
     * such as town or city. Not manditory if the address type is consolidated.
     * @param country  String. Mandatory. Maximum length of 2 characters. The actor country, given
     * as a 2-letter country code (ISO 3166-1).
     *
     * @return Boolean. Whether the value has validated and stored correctly or not.
     */
    fun setActor(
        typeId: Int,
        name: String?,
        addressType: String?,
        address1: String?,
        address2: String?,
        postalcode: String?,
        location: String?,
        country: String?
    ): Boolean {
        return if (typeId != ACTOR_CR && typeId != ACTOR_UCR && typeId != ACTOR_UDR) {
            false
        } else {
            actors[typeId].name = validateStr(name, true, 70)
            if (addressType != null) actors[typeId].addressType = validateStr(addressType, version!! >= 2.0f, 1)
            actors[typeId].address1 = validateStr(address1, false, 70)
            actors[typeId].address2 = validateStr(
                address2, false,
                if (actors[typeId].addressType == ADDTYPE_COMBINED) 70 else 16
            )
            actors[typeId].postcode = validateStr(postalcode, true, 16)
            actors[typeId].location = validateStr(location, true, 35)
            actors[typeId].country = validateStr(country, true, 2)
            validateDependancies(typeId)
        }
    }

    private fun validateData(rawData: String?): java.util.ArrayList<QRBillException>? {
        val errors: java.util.ArrayList<QRBillException> = java.util.ArrayList<QRBillException>()
        if (rawData == null || rawData.length == 0) errors.add(QRBillException(1, "Input data empty or null."))
        if (rawData!!.length > 997) errors.add(QRBillException(2, "Input data exceeds maximum allowed limit."))
        val qrData: Array<String> = rawData.trim { it <= ' ' }.split("\n").toTypedArray()
        if (qrData.size < 25) errors.add(QRBillException(3, "Malformed Data - insufficient fields."))
        if (!setVersion(qrData[1])) errors.add(QRBillException(4, "Version invalid or not supported"))
        actors[0] = Actor(ACTOR_CR)
        actors[1] = Actor(ACTOR_UCR)
        actors[2] = Actor(ACTOR_UDR)
        val structure = structure
        var refType: String? = REFTYPE_NON
        var actorIds: Array<Data>
        for (i in qrData.indices) {
            val item = qrData[i]
            val key = if (structure!!.size > i) structure[i] else Data.NONE
            when (key) {
                Data.NONE, Data.VERSION -> {}
                Data.QRTYPE -> if (!setQrType(item)) errors.add(QRBillException(5, "QR Type invalid or not supported"))
                Data.CODING -> if (!setCodingType(item)) errors.add(QRBillException(6, "Valid Coding type Missing"))
                Data.ACCOUNT -> if (!setIBAN(item)) errors.add(QRBillException(7, "Valid IBAN Missing"))
                Data.CR_NAME, Data.UCR_NAME, Data.UDR_NAME -> {
                    actorIds = arrayOf(Data.CR_NAME, Data.UCR_NAME, Data.UDR_NAME)
                    var j = 0
                    while (j < actorIds.size) {
                        if (actorIds[j] == key) {
                            actors[j].name = validateStr(item, true, 70)
                        }
                        j++
                    }
                }
                Data.CR_ADDRESS1, Data.UCR_ADDRESS1, Data.UDR_ADDRESS1 -> {
                    actorIds = arrayOf(Data.CR_ADDRESS1, Data.UCR_ADDRESS1, Data.UDR_ADDRESS1)
                    var j = 0
                    while (j < actorIds.size) {
                        if (actorIds[j] == key) {
                            actors[j].address1 = validateStr(item, false, 70)
                        }
                        j++
                    }
                }
                Data.CR_ADDRESS2, Data.UCR_ADDRESS2, Data.UDR_ADDRESS2 -> {
                    actorIds = arrayOf(Data.CR_ADDRESS2, Data.UCR_ADDRESS2, Data.UDR_ADDRESS2)
                    var j = 0
                    while (j < actorIds.size) {
                        if (actorIds[j] == key && actors[j].addressType != null) {
                            actors[j].address2 = validateStr(
                                item, false,
                                if (actors[j].addressType == ADDTYPE_COMBINED) 70 else 16
                            )
                        }
                        j++
                    }
                }
                Data.CR_POSTCODE, Data.UCR_POSTCODE, Data.UDR_POSTCODE -> {
                    actorIds = arrayOf(Data.CR_POSTCODE, Data.UCR_POSTCODE, Data.UDR_POSTCODE)
                    var j = 0
                    while (j < actorIds.size) {
                        if (actorIds[j] == key) {
                            actors[j].postcode = validateStr(item, true, 16)
                        }
                        j++
                    }
                }
                Data.CR_LOCATION, Data.UCR_LOCATION, Data.UDR_LOCATION -> {
                    actorIds = arrayOf(Data.CR_LOCATION, Data.UCR_LOCATION, Data.UDR_LOCATION)
                    var j = 0
                    while (j < actorIds.size) {
                        if (actorIds[j] == key) {
                            actors[j].location = validateStr(item, true, 35)
                        }
                        j++
                    }
                }
                Data.CR_COUNTRY, Data.UCR_COUNTRY, Data.UDR_COUNTRY -> {
                    actorIds = arrayOf(Data.CR_COUNTRY, Data.UCR_COUNTRY, Data.UDR_COUNTRY)
                    var j = 0
                    while (j < actorIds.size) {
                        if (actorIds[j] == key) {
                            actors[j].country = validateStr(item.uppercase(Locale.getDefault()), true, 2)
                        }
                        j++
                    }
                }
                Data.CR_ADDTYPE, Data.UCR_ADDTYPE, Data.UDR_ADDTYPE -> {
                    actorIds = arrayOf(Data.CR_ADDTYPE, Data.UCR_ADDTYPE, Data.UDR_ADDTYPE)
                    var j = 0
                    while (j < actorIds.size) {
                        if (actorIds[j] == key) {
                            actors[j].addressType =
                                validateStr(item.uppercase(Locale.getDefault()), version!! >= 2.0f, 1)
                        }
                        j++
                    }
                }
                Data.AMOUNT -> setAmount(item)
                Data.CURRENCY -> if (!setCurrency(item)) errors.add(QRBillException(8, "Valid Currency Missing"))
                Data.DUEDATE -> setDueDate(item)
                Data.REF_TYPE -> refType = item
                Data.REF -> if (!setReference(refType, item)) errors.add(QRBillException(9, "Valid Reference Missing"))
                Data.ALTSCHEMA1 -> setAlternativeSchema(item, 0)
                Data.ALTSCHEMA2 -> setAlternativeSchema(item, 1)
                Data.UNSTR_MSG -> setUnstructuredMsg(item)
                Data.TRAILER -> setTrailer(item)
                Data.BILLINFO -> setBillInfo(item)
            }
        }
        if (reference == null) setReference(REFTYPE_NON, "")
        var allGood = true
        for (i in actors.indices) if (!validateDependancies(i)) allGood = false
        if (!allGood) errors.add(QRBillException(10, "Mandatory actor dependancies not met."))
        return null
    }

    private fun setCodingType(codingType: String): Boolean {
        return try {
            val type: Int = codingType.toInt()
            setCodingType(type)
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun setAmount(amt: String): Boolean {
        return try {
            setAmount(amt.toFloat())
        } catch (e: NumberFormatException) {
            setAmount(-1.0f)
        }
    }

    private fun setDueDate(dueDate: String?): Boolean {
        return if (dueDate == null || dueDate.length == 0) {
            setDueDate(0, 0, 0)
        } else {
            val tempDate: Array<String> = dueDate.split("-").toTypedArray()
            if (tempDate.size != 3) {
                setDueDate(0, 0, 0)
            } else {
                try {
                    val year: Int = tempDate[0].toInt()
                    val month: Int = tempDate[1].toInt()
                    val day: Int = tempDate[2].toInt()
                    setDueDate(year, month, day)
                } catch (e: NumberFormatException) {
                    setDueDate(0, 0, 0)
                }
            }
        }
    }

    private fun validateStr(entry: String?, required: Boolean, maxLen: Int = 0): String? {
        return if (entry == null || entry.length == 0) {
            if (required) {
                null
            } else {
                ""
            }
        } else if (maxLen == 0 || maxLen >= entry.length) {
            entry
        } else {
            if (required) {
                null
            } else {
                ""
            }
        }
    }

    private fun validateDependancies(typeId: Int): Boolean {
        if (typeId > 2 || typeId < 0) return false
        val test = StringBuffer()
        if (actors[typeId].name != null) test.append(actors[typeId].name.trim { it <= ' ' })
        if (actors[typeId].address1 != null) test.append(actors[typeId].address1.trim { it <= ' ' })
        if (actors[typeId].address2 != null) test.append(actors[typeId].address2.trim { it <= ' ' })
        if (actors[typeId].postcode != null) test.append(actors[typeId].postcode.trim { it <= ' ' })
        if (actors[typeId].location != null) test.append(actors[typeId].location.trim { it <= ' ' })
        if (actors[typeId].country != null) test.append(actors[typeId].country.trim { it <= ' ' })
        val hasEntry: Boolean = test.length > 0
        var valid = true
        if (hasEntry) {
            if (actors[typeId].name == null || actors[typeId].name!!.length == 0) valid = false
            if (version!! >= 2.0f) {
                if (actors[typeId].addressType == null || actors[typeId].addressType!!.length == 0) {
                    valid = false
                } else when (actors[typeId].addressType) {
                    ADDTYPE_STRUCTURED -> {
                        if (actors[typeId].address1 == null || actors[typeId].address1!!.length == 0) valid = false
                        if (actors[typeId].postcode == null || actors[typeId].postcode!!.length == 0) valid = false
                        if (actors[typeId].location == null || actors[typeId].location!!.length == 0) valid = false
                        if (actors[typeId].country == null || actors[typeId].country!!.length == 0) valid = false
                    }
                    ADDTYPE_COMBINED -> {
                        if (actors[typeId].address1 == null || actors[typeId].address1!!.length == 0) valid = false
                        if (actors[typeId].address2 == null || actors[typeId].address2!!.length == 0) valid = false
                    }
                    else -> valid = false
                }
            } else {
                if (actors[typeId].postcode == null || actors[typeId].postcode!!.length == 0) valid = false
                if (actors[typeId].location == null || actors[typeId].location!!.length == 0) valid = false
                if (actors[typeId].country == null || actors[typeId].country!!.length == 0) valid = false
            }
        } else if (typeId == ACTOR_CR) {
            valid = false
        }
        if (valid && typeId > 0) {
            if (actors[typeId].addressType == null) actors[typeId].addressType = ""
            if (actors[typeId].name == null) actors[typeId].name = ""
            if (actors[typeId].address1 == null) actors[typeId].address1 = ""
            if (actors[typeId].address2 == null) actors[typeId].address2 = ""
            if (actors[typeId].postcode == null) actors[typeId].postcode = ""
            if (actors[typeId].location == null) actors[typeId].location = ""
            if (actors[typeId].country == null) actors[typeId].country = ""
        }
        return valid
    }

    private fun formatAmountAsString(amt: Float): String {
        var pointPos = -1
        var amount = amt.toString()
        for (i in 0 until amount.length) {
            try {
                amount[i].toString().toInt()
            } catch (e: NumberFormatException) {
                pointPos = i
                break
            }
        }
        var decimals = ".00"
        if (pointPos > -1) decimals = amount.substring(pointPos + 1)
        if (decimals.length > 2) {
            amount = amount.substring(0, amount.length - decimals.length + 2)
        } else if (decimals.length == 1) {
            amount += "0"
        }
        return amount
    }

    private val structure: Array<Data>?
        private get() {
            if (version == null) return null
            return if (version == 2.00f) {
                version2
            } else if (version!! >= 1.00f) {
                version1
            } else {
                version = null
                null
            }
        }

    /**
     * Exception thrown upon validation failure whenever the class is instantiated with raw QR code
     * or [.getQRCode] is called. This exception object contains both an error code (int) and
     * error message (String).
     *
     * The error codes and messages that may be returned are as follows:
     *
     *
     *  1. Input data empty or null.
     *  1. Input data exceeds maximum allowed limit.
     *  1. Malformed Data - insufficient fields.
     *  1. Version invalid or not supported.
     *  1. QR Type invalid or not supported.
     *  1. Valid Coding type Missing.
     *  1. Valid IBAN Missing.
     *  1. Valid Currency Missing.
     *  1. Valid Reference Missing.
     *  1. Mandatory actor dependencies not met.
     *
     */
    inner class QRBillException
    /**
     * Constructor for instantiating a QRBillException object.
     *
     * @param errorId Integer. A custom identifying error ID code.
     * @param msg String. The error description (overrides [java.lang.Exception]).
     */(
        /**
         * Returns a code corrisponding to the exception caught.
         *
         * @return Integer. The error ID code.
         */
        val errorId: Int, msg: String?
    ) : Exception(msg)

    private inner class Actor(val type: Int) {
        var name: String? = ""
        var addressType: String? = ADDTYPE_STRUCTURED
        var address1: String? = ""
        var address2: String? = ""
        var postcode: String? = ""
        var location: String? = ""
        var country: String? = ""

        init {
            addressType = ADDTYPE_STRUCTURED
        }
    }

    private object Modulo10 {
        private val pattern = arrayOf(
            intArrayOf(0, 9, 4, 6, 8, 2, 7, 1, 3, 5),
            intArrayOf(9, 4, 6, 8, 2, 7, 1, 3, 5, 0),
            intArrayOf(4, 6, 8, 2, 7, 1, 3, 5, 0, 9),
            intArrayOf(6, 8, 2, 7, 1, 3, 5, 0, 9, 4),
            intArrayOf(8, 2, 7, 1, 3, 5, 0, 9, 4, 6),
            intArrayOf(2, 7, 1, 3, 5, 0, 9, 4, 6, 8),
            intArrayOf(7, 1, 3, 5, 0, 9, 4, 6, 8, 2),
            intArrayOf(1, 3, 5, 0, 9, 4, 6, 8, 2, 7),
            intArrayOf(3, 5, 0, 9, 4, 6, 8, 2, 7, 1),
            intArrayOf(5, 0, 9, 4, 6, 8, 2, 7, 1, 3)
        )
        private val checkDigits = intArrayOf(
            0, 9, 8, 7, 6, 5, 4, 3, 2, 1
        )
        private const val codeLength = 27
        fun validate(input: String?): Boolean {
            var input = input
            if (input != null && input.length > 0) {
                input = input.replace(" ".toRegex(), "").trim { it <= ' ' }
            }
            return if (input == null) false else try {
                val check = getCheckDigit(input)
                val td: String = input.substring(input.length - 1)
                val endDigit: Int = td.toInt()
                endDigit == check
            } catch (e: Exception) {
                false
            }
        }

        fun getCheckDigit(input: String): Int {
            if (input.length < codeLength) return -1
            val bd: String = input.substring(0, input.length - 1)
            var position = 0
            return try {
                for (i in 0 until bd.length) {
                    val digit: Int = java.lang.Character.toString(bd[i]) as String?. toInt ()
                    position = pattern[position][digit]
                }
                checkDigits[position]
            } catch (e: Exception) {
                -1
            }
        }
    }

    companion object {
        /**
         * QR Type Identifier: Swiss Payments Code
         */
        const val QRTYPE_SPC = "SPC"

        /**
         * Character Set: Latin (ISO-8859-1)
         */
        const val CODING_LATIN_1 = 1

        /**
         * Actor Type: Creditor
         */
        const val ACTOR_CR = 0

        /**
         * Actor Type: Ultimate creditor
         */
        const val ACTOR_UCR = 1

        /**
         * Actor Type: Ultimate debtor
         */
        const val ACTOR_UDR = 2

        /**
         * Address Type: Structured
         */
        const val ADDTYPE_STRUCTURED = "S"

        /**
         * Address Type: Combined
         */
        const val ADDTYPE_COMBINED = "K"

        /**
         * Currency: CHF - Swiss Francs
         */
        const val CURRENCY_CHF = "CHF"

        /**
         * Currency: EUR - Euro
         */
        const val CURRENCY_EUR = "EUR"

        /**
         * Reference Type: QR Reference
         */
        const val REFTYPE_QRR = "QRR"

        /**
         * Reference Type: Creditor Reference (ISO 11649)
         */
        const val REFTYPE_SCOR = "SCOR"

        /**
         * Reference Type: None
         */
        const val REFTYPE_NON = "NON"

        /**
         * Trailer: End Payment Data (EPD)
         */
        const val TRAILER_EPD = "EPD"
        private const val VERSION_SUPPORTED = 2.00f
    }
}