library qr_bill;

class QRBill {
  String? _qrType;
  double? _version;
  int? _codingType;
  String? _account;
  double? _amount;
  String? _currency;
  String? _dueDate;
  String? _referenceType;
  String? _reference;
  String? _unstructuredMsg = "";
  String? _trailer;
  String _billInfo = "";
  final List<String> _as = ["", ""];
  final List<Actor> _actors = [
    Actor(actorCR),
    Actor(actorUCR),
    Actor(actorUDR)
  ];

  static const String qrTypeSpc = "SPC";
  static const int codingLatin1 = 1;
  static const int actorCR = 0;
  static const int actorUCR = 1;
  static const int actorUDR = 2;
  static const String addTypeStructured = "S";
  static const String addTypeCombined = "K";
  static const String currencyCHF = "CHF";
  static const String currencyEUR = "EUR";
  static const String refTypeQRR = "QRR";
  static const String refTypeSCOR = "SCOR";
  static const String refTypeNON = "NON";
  static const String trailerEPD = "EPD";

  static const double versionSupported = 2.00;

  static const List<Data> _version1 = [
    Data.qrType,
    Data.version,
    Data.coding,
    Data.account,
    Data.crName,
    Data.crAddress1,
    Data.crAddress2,
    Data.crPostcode,
    Data.crLocation,
    Data.crCountry,
    Data.ucrName,
    Data.ucrAddress1,
    Data.ucrAddress2,
    Data.ucrPostcode,
    Data.urcLocation,
    Data.ucrCountry,
    Data.amount,
    Data.currency,
    Data.dueDate,
    Data.udrName,
    Data.udrAddress1,
    Data.udrAddress2,
    Data.udrPostcode,
    Data.udrLocation,
    Data.udrCountry,
    Data.refType,
    Data.ref,
    Data.unstrMsg,
    Data.altSchema1,
    Data.altSchema2
  ];

  static const List<Data> _version2 = [
    Data.qrType,
    Data.version,
    Data.coding,
    Data.account,
    Data.crAddType,
    Data.crName,
    Data.crAddress1,
    Data.crAddress2,
    Data.crPostcode,
    Data.crLocation,
    Data.crCountry,
    Data.ucrAddType,
    Data.ucrName,
    Data.ucrAddress1,
    Data.ucrAddress2,
    Data.ucrPostcode,
    Data.urcLocation,
    Data.ucrCountry,
    Data.amount,
    Data.currency,
    Data.udrAddType,
    Data.udrName,
    Data.udrAddress1,
    Data.udrAddress2,
    Data.udrPostcode,
    Data.udrLocation,
    Data.udrCountry,
    Data.refType,
    Data.ref,
    Data.unstrMsg,
    Data.trailer,
    Data.billInfo,
    Data.altSchema1,
    Data.altSchema2
  ];

  QRBill({String data = "", bool strict = true}) {
    if (data.isEmpty) {
      setQrType(QRBill.qrTypeSpc);
      setVersion(QRBill.versionSupported);
      setCodingType(QRBill.codingLatin1);
      setReference();
      setAmount();
      setCurrency(QRBill.currencyCHF);
      setTrailer(QRBill.trailerEPD);
    } else {
      _validateData(data);
    }
  }

  @override
  String toString() {
    List<Data> structure = _getStructure();
    StringBuffer out = StringBuffer();
    if (structure.isNotEmpty) {
      for (Data element in structure) {
        switch (element) {
          case Data.none:
          // Ignore
            break;
          case Data.qrType:
            out.writeln(getQrType());
            break;
          case Data.version:
            out.writeln(getFormattedVersion());
            break;
          case Data.coding:
            out.writeln(getCodingType());
            break;
          case Data.account:
            out.writeln(getIBAN());
            break;
          case Data.amount:
            if (getAmount() != null && getAmount()! > 0) {
              out.write(_formatAmountAsString(getAmount()));
            }
            out.writeln();
            break;
          case Data.currency:
            out.writeln(getCurrency());
            break;
          case Data.dueDate:
            List<int> dueDate = getDueDate();
            if (dueDate.isEmpty) {
              out.writeln();
            } else {
              out.writeln("${dueDate[0]}-${dueDate[1]}-${dueDate[2]}");
            }
            break;
          case Data.refType:
            out.writeln(getReferenceType());
            break;
          case Data.ref:
            out.writeln(getReference());
            break;
          case Data.crAddType:
            out.writeln(getActorAddressType(QRBill.actorCR));
            break;
          case Data.crName:
            out.writeln(getActorName(QRBill.actorCR));
            break;
          case Data.crAddress1:
            out.writeln(getActorStreet(QRBill.actorCR));
            break;
          case Data.crAddress2:
            out.writeln(getActorHouseNumber(QRBill.actorCR));
            break;
          case Data.crPostcode:
            out.writeln(getActorPostcode(QRBill.actorCR));
            break;
          case Data.crLocation:
            out.writeln(getActorLocation(QRBill.actorCR));
            break;
          case Data.crCountry:
            out.writeln(getActorCountry(QRBill.actorCR));
            break;
          case Data.ucrAddType:
            out.writeln(getActorAddressType(QRBill.actorUCR));
            break;
          case Data.ucrName:
            out.writeln(getActorName(QRBill.actorUCR));
            break;
          case Data.ucrAddress1:
            out.writeln(getActorStreet(QRBill.actorUCR));
            break;
          case Data.ucrAddress2:
            out.writeln(getActorHouseNumber(QRBill.actorUCR));
            break;
          case Data.ucrPostcode:
            out.writeln(getActorPostcode(QRBill.actorUCR));
            break;
          case Data.urcLocation:
            out.writeln(getActorLocation(QRBill.actorUCR));
            break;
          case Data.ucrCountry:
            out.writeln(getActorCountry(QRBill.actorUCR));
            break;
          case Data.udrAddType:
            out.writeln(getActorAddressType(QRBill.actorUDR));
            break;
          case Data.udrName:
            out.writeln(getActorName(QRBill.actorUDR));
            break;
          case Data.udrAddress1:
            out.writeln(getActorStreet(QRBill.actorUDR));
            break;
          case Data.udrAddress2:
            out.writeln(getActorHouseNumber(QRBill.actorUDR));
            break;
          case Data.udrPostcode:
            out.writeln(getActorPostcode(QRBill.actorUDR));
            break;
          case Data.udrLocation:
            out.writeln(getActorLocation(QRBill.actorUDR));
            break;
          case Data.udrCountry:
            out.writeln(getActorCountry(QRBill.actorUDR));
            break;
          case Data.unstrMsg:
            out.writeln(getUnstructuredMsg() ?? "");
            break;
          case Data.altSchema1:
            List<String> as1 = getAlternativeSchema();
            out.writeln(as1[0]);
            break;
          case Data.altSchema2:
            List<String> as2 = getAlternativeSchema();
            out.writeln(as2[1]);
            break;
          case Data.trailer:
            out.writeln(getTrailer());
            break;
          case Data.billInfo:
            out.writeln(getBillInfo());
            break;
        }
      }
    }

    return out.toString().trim();
  }

  String getQRCode() => toString();

  bool isValid() => _validateData(toString()).isEmpty;

  List<QRBillException> get qrExceptions => _validateData(toString());

  String? getQrType() => _qrType;

  double? getVersion() => _version;

  String getFormattedVersion() {
    if (_version == null) return "";
    String fv = ((_version! * 100).floor()).toString();
    if (fv.length < 4) fv = "0$fv";
    return fv;
  }

  int? getCodingType() => _codingType;

  String? getIBAN() => _account;

  double? getAmount() => _amount;

  String? getCurrency() => _currency;

  String? getReferenceType() => _referenceType;

  String? getReference() => _reference;

  String getAdditionalInfo() => getUnstructuredMsg() ?? "";

  String getActorName(int actorType) => _actors[actorType].name ?? "";

  String getActorAddressType(int actorType) =>
      _actors[actorType].addressType ?? "";

  String getActorStreet(int actorType) => _actors[actorType].address1 ?? "";

  String getActorHouseNumber(int actorType) =>
      _actors[actorType].address2 ?? "";

  String getActorPostcode(int actorType) => _actors[actorType].postcode ?? "";

  String getActorLocation(int actorType) => _actors[actorType].location ?? "";

  String getActorCountry(int actorType) => _actors[actorType].country ?? "";

  String? getUnstructuredMsg() => _unstructuredMsg;

  String? getTrailer() => _trailer;

  String? getBillInfo() => _billInfo;

  List<String> getAlternativeSchema([int index = -1]) {
    if (index > 1 || index < 0) {
      return _as;
    } else {
      if (_as[index].length < 3) {
        return [];
      } else {
        String token = _as[index].substring(0, 2);
        String del = _as[index][2];
        List<String> data = _as[index].substring(3).split(del);
        List<String> retArray = [token, del];
        for (int i = 2; i < retArray.length; i++) {
          retArray.add(data[i - 2]);
        }
        return retArray;
      }
    }
  }

  List<int> getDueDate() {
    if (_dueDate == null || _dueDate!.isEmpty) {
      return [];
    } else {
      List<String> tempDate = _dueDate!.split("-");
      if (tempDate.length != 3) {
        return [];
      } else {
        int? year = int.tryParse(tempDate[0]);
        if (year == null) return [];
        int? month = int.tryParse(tempDate[1]);
        if (month == null) return [];
        int? day = int.tryParse(tempDate[2]);
        if (day == null) return [];
        return [year, month, day];
      }
    }
  }

  bool setQrType(String? qrType) {
    _qrType = QRBill.qrTypeSpc;
    if (qrType != null && qrType == QRBill.qrTypeSpc) {
      return true;
    } else {
      return false;
    }
  }

  bool setVersion(var version) {
    if (version is String) {
      if (version.length != 4) {
        return false;
      } else {
        double? v = double.tryParse(version);
        if (v != null) {
          return setVersion(v / 100);
        } else {
          return false;
        }
      }
    } else if (version is double) {
      if (version <= QRBill.versionSupported) {
        _version = version;
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  bool setCodingType(int codingType) {
    List<int> codingTypes = [QRBill.codingLatin1];
    bool valid = false;
    for (int code in codingTypes) {
      if (codingType == code) valid = true;
    }
    if (valid) {
      _codingType = codingType;
      return true;
    } else {
      return false;
    }
  }

  bool setIBAN(String? iban) {
    _account = iban;
    if (iban == null) return false;
    iban = iban.replaceAll(" ", "").trim();
    for (int i = 0; i < iban.length; i++) {
      if (!"1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"
          .contains(iban[i].toUpperCase())) return false;
    }
    iban = _validateStr(iban, true, 21);
    if (iban == null) {
      return false;
    } else {
      _account = iban;
      if (_account!.toUpperCase().startsWith("CH")) {
        return true;
      } else if (_account!.toUpperCase().startsWith("LI")) {
        return true;
      } else {
        return false;
      }
    }
  }

  bool setAmount([double amt = 0.0]) {
    if (amt < 0) {
      _amount = 0.0;
      return true;
    } else {
      String amount = _formatAmountAsString(amt);
      _amount = double.tryParse(amount);
      if (_amount != null && amount.length > 12) {
        return false;
      } else {
        return true;
      }
    }
  }

  bool setCurrency(String? currency) {
    if ((currency == null || currency.isEmpty)) {
      return false;
    } else {
      List<String> currencies = [QRBill.currencyCHF, QRBill.currencyEUR];
      bool valid = false;
      for (String cur in currencies) {
        if (currency.toUpperCase() == cur) valid = true;
      }
      _currency = currency.toUpperCase();
      return valid;
    }
  }

  bool setDueDate(int year, int month, int day) {
    bool isValid = true;

    if (year < 2018 || year > 9999) isValid = false;

    if (month < 1 || month > 12) isValid = false;

    List<int> maxDays = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
    if (day < 1 || day > maxDays[month - 1]) isValid = false;

    if (month == 2 && day == 29) {
      if (year % 4 > 0) isValid = false;
      if (year % 100 == 0 && year % 400 > 0) isValid = false;
    }

    if (isValid) {
      _dueDate =
      "$year-${("00$month").substring(month.toString().length)}-${("00$day").substring(day.toString().length)}";
    } else {
      _dueDate = "";
    }
    return true;
  }

  bool setReference([String? refType, String? ref]) {
    if (refType == null || ref == null) {
      refType = QRBill.refTypeNON;
      ref = null;
    }

    bool valid = false;
    refType = refType.toUpperCase();
    List<String> refTypes = [
      QRBill.refTypeQRR,
      QRBill.refTypeSCOR,
      QRBill.refTypeNON
    ];
    for (String type in refTypes) {
      if (refType == type.toUpperCase()) valid = true;
    }
    if (valid) _referenceType = refType;

    if (_referenceType == QRBill.refTypeNON) ref = null;

    if (ref != null) ref = ref.replaceAll(" ", "").trim();

    switch (refType) {
      case QRBill.refTypeQRR:
        _reference = _validateStr(ref!, true, 27);
        if (_reference == null || !Modulo10.validate(_reference!)) {
          valid = false;
        }
        break;
      case QRBill.refTypeSCOR:
        _reference = _validateStr(ref!, true, 25);
        if (_reference == null) valid = false;
        break;
      case QRBill.refTypeNON:
        _reference = "";
        break;
      default:
        valid = false;
    }

    return valid;
  }

  bool setAdditionalInfo(String info) {
    return setUnstructuredMsg(info);
  }

  bool setUnstructuredMsg(String? unstructuredMsg) {
    _unstructuredMsg = _validateStr(unstructuredMsg, false, 140);
    return _unstructuredMsg != null && unstructuredMsg!.length > 140;
  }

  bool setTrailer(String? trailer) {
    if (trailer == null) return false;
    trailer = trailer.toUpperCase().trim();
    if (trailer.length != 3) return false;

    switch (trailer) {
      case trailerEPD:
        _trailer = trailer;
        return true;
      default:
        return false;
    }
  }

  bool setBillInfo(String? billInfo) {
    _billInfo = _validateStr(billInfo, false, 140) ?? "";
    return _billInfo.length <= 140;
  }

  bool setAlternativeSchema(
      {List<String>? schemas, String? schema, int index = -1}) {
    if (schemas != null) {
      for (int i = 0; i < schemas.length; i++) {
        if (!setAlternativeSchema(schema: schemas[i], index: i)) return false;
      }
      return true;
    } else if (schema != null && index >= 0) {
      if (index < 0 || index > 1) {
        return false;
      } else {
        _as[index] = _validateStr(schema, false, 100) ?? "";
        return _as[index].isNotEmpty;
      }
    } else {
      _as[0] = "";
      _as[1] = "";
      return true;
    }
  }

  bool setActor(
      {required int typeId,
        String? name,
        String? addressType,
        String? address1,
        String? address2,
        String? postalcode,
        String? location,
        String? country}) {
    if (typeId != QRBill.actorCR &&
        typeId != QRBill.actorUCR &&
        typeId != QRBill.actorUDR) {
      return false;
    } else {
      _actors[typeId].name = _validateStr(name, true, 70);
      if (addressType != null) {
        _actors[typeId].addressType =
            _validateStr(addressType, _version != null && _version! >= 2.0, 1);
      }
      _actors[typeId].address1 = _validateStr(address1, false, 70);
      _actors[typeId].address2 = _validateStr(address2, false,
          _actors[typeId].addressType == addTypeCombined ? 70 : 16);
      _actors[typeId].postcode = _validateStr(postalcode, true, 16);
      _actors[typeId].location = _validateStr(location, true, 35);
      _actors[typeId].country = _validateStr(country, true, 2);

      return _validateDependancies(typeId);
    }
  }

  List<QRBillException> _validateData(String rawData) {
    List<QRBillException> errors = [];
    if (rawData.isEmpty) {
      errors.add(QRBillException(1, "Input data empty or null."));
    }

    if (rawData.length > 997) {
      errors
          .add(QRBillException(2, "Input data exceeds maximum allowed limit."));
    }

    List<String> qrData = rawData.trim().split("\n");
    if (qrData.length < 25) {
      errors.add(QRBillException(3, "Malformed Data - insufficient fields."));
    }

    if (!setVersion(qrData[1])) {
      errors.add(QRBillException(4, "Version invalid or not supported"));
    }

    _actors[0] = Actor(QRBill.actorCR);
    _actors[1] = Actor(QRBill.actorUCR);
    _actors[2] = Actor(QRBill.actorUDR);
    List<Data> structure = _getStructure();
    String refType = QRBill.refTypeNON;
    List<Data> actorIds;

    for (int i = 0; i < qrData.length; i++) {
      String item = qrData[i];
      Data key = structure.length > i ? structure[i] : Data.none;
      switch (key) {
        case Data.none:
        case Data.version:
        // Ignore
          break;
        case Data.qrType:
          if (!setQrType(item)) {
            errors.add(QRBillException(5, "QR Type invalid or not supported"));
          }
          break;
        case Data.coding: // Coding Type
          if (!_setCodingType(item)) {
            errors.add(QRBillException(6, "Valid Coding type Missing"));
          }
          break;
        case Data.account: // Konto
          if (!setIBAN(item)) {
            errors.add(QRBillException(7, "Valid IBAN Missing"));
          }
          break;
        case Data.crName:
        case Data.ucrName:
        case Data.udrName:
          actorIds = [Data.crName, Data.ucrName, Data.udrName];
          for (int j = 0; j < actorIds.length; j++) {
            if (actorIds[j] == key) {
              _actors[j].name = _validateStr(item, true, 70);
            }
          }
          break;
        case Data.crAddress1:
        case Data.ucrAddress1:
        case Data.udrAddress1:
          actorIds = [Data.crAddress1, Data.ucrAddress1, Data.udrAddress1];
          for (int j = 0; j < actorIds.length; j++) {
            if (actorIds[j] == key) {
              _actors[j].address1 = _validateStr(item, false, 70);
            }
          }
          break;
        case Data.crAddress2:
        case Data.ucrAddress2:
        case Data.udrAddress2:
          actorIds = [Data.crAddress2, Data.ucrAddress2, Data.udrAddress2];
          for (int j = 0; j < actorIds.length; j++) {
            if (actorIds[j] == key) {
              _actors[j].address2 = _validateStr(item, false,
                  _actors[j].addressType == addTypeCombined ? 70 : 16);
            }
          }
          break;
        case Data.crPostcode:
        case Data.ucrPostcode:
        case Data.udrPostcode:
          actorIds = [Data.crPostcode, Data.ucrPostcode, Data.udrPostcode];
          for (int j = 0; j < actorIds.length; j++) {
            if (actorIds[j] == key) {
              _actors[j].postcode = _validateStr(item, true, 16);
            }
          }
          break;
        case Data.crLocation:
        case Data.urcLocation:
        case Data.udrLocation:
          actorIds = [Data.crLocation, Data.urcLocation, Data.udrLocation];
          for (int j = 0; j < actorIds.length; j++) {
            if (actorIds[j] == key) {
              _actors[j].location = _validateStr(item, true, 35);
            }
          }
          break;
        case Data.crCountry:
        case Data.ucrCountry:
        case Data.udrCountry:
          actorIds = [Data.crCountry, Data.ucrCountry, Data.udrCountry];
          for (int j = 0; j < actorIds.length; j++) {
            if (actorIds[j] == key) {
              _actors[j].country = _validateStr(item.toUpperCase(), true, 2);
            }
          }
          break;
        case Data.crAddType:
        case Data.ucrAddType:
        case Data.udrAddType:
          actorIds = [Data.crAddType, Data.ucrAddType, Data.udrAddType];
          for (int j = 0; j < actorIds.length; j++) {
            if (actorIds[j] == key) {
              _actors[j].addressType = _validateStr(
                  item.toUpperCase(), _version != null && _version! >= 2.0, 1);
            }
          }
          break;
        case Data.amount:
          _setAmount(item);
          break;
        case Data.currency:
          if (!setCurrency(item)) {
            errors.add(QRBillException(8, "Valid Currency Missing"));
          }
          break;
        case Data.dueDate:
          _setDueDate(item);
          break;
        case Data.refType:
          refType = item;
          break;
        case Data.ref:
          if (!setReference(refType, item)) {
            errors.add(QRBillException(9, "Valid Reference Missing"));
          }
          break;
        case Data.altSchema1:
          setAlternativeSchema(schema: item, index: 0);
          break;
        case Data.altSchema2:
          setAlternativeSchema(schema: item, index: 1);
          break;
        case Data.unstrMsg:
          setUnstructuredMsg(item);
          break;
        case Data.trailer:
          setTrailer(item);
          break;
        case Data.billInfo:
          setBillInfo(item);
          break;
      }
    }

    bool allGood = true;
    for (int i = 0; i < _actors.length; i++) {
      if (!_validateDependancies(i)) allGood = false;
    }

    if (!allGood) {
      errors.add(QRBillException(10, "Mandatory actor dependencies not met."));
    }

    return errors;
  }

  bool _setCodingType(String codingType) {
    int? type = int.tryParse(codingType);
    if (type == null) {
      setCodingType(QRBill.codingLatin1);
      return false;
    } else {
      return setCodingType(type);
    }
  }

  bool _setAmount(String amt) {
    double? amount = double.tryParse(amt);
    if (amount == null) {
      return false;
    } else {
      return setAmount(amount);
    }
  }

  bool _setDueDate(String? dueDate) {
    if (dueDate == null || dueDate.isEmpty) {
      return setDueDate(0, 0, 0);
    } else {
      List<String> tempDate = dueDate.split("-");
      if (tempDate.length != 3) {
        return setDueDate(0, 0, 0);
      } else {
        int? year = int.tryParse(tempDate[0]);
        int? month = int.tryParse(tempDate[1]);
        int? day = int.tryParse(tempDate[2]);
        if (year != null && month != null && day != null) {
          return setDueDate(year, month, day);
        } else {
          return setDueDate(0, 0, 0);
        }
      }
    }
  }

  String? _validateStr(String? entry, bool required, [int maxLen = 0]) {
    if (entry == null || entry.isEmpty) {
      if (required) {
        return null;
      } else {
        return "";
      }
    } else if (maxLen == 0 || entry.length <= maxLen) {
      return entry;
    } else {
      if (required) {
        return null;
      } else {
        return "";
      }
    }
  }

  bool _validateDependancies(int typeId) {
    if (typeId > 2 || typeId < 0) return false;

    StringBuffer test = StringBuffer();
    if (_actors[typeId].name != null) test.write(_actors[typeId].name!.trim());
    if (_actors[typeId].address1 != null) {
      test.write(_actors[typeId].address1!.trim());
    }
    if (_actors[typeId].address2 != null) {
      test.write(_actors[typeId].address2!.trim());
    }
    if (_actors[typeId].postcode != null) {
      test.write(_actors[typeId].postcode!.trim());
    }
    if (_actors[typeId].location != null) {
      test.write(_actors[typeId].location!.trim());
    }
    if (_actors[typeId].country != null) {
      test.write(_actors[typeId].country!.trim());
    }
    bool hasEntry = test.length > 0;
    bool valid = true;

    if (hasEntry) {
      if (_actors[typeId].name == null || _actors[typeId].name!.isEmpty) {
        valid = false;
      }
      if (_version != null && _version! >= 2.0) {
        if (_actors[typeId].addressType == null ||
            _actors[typeId].addressType!.isEmpty) {
          valid = false;
        } else {
          switch (_actors[typeId].addressType) {
            case addTypeStructured:
              if (_actors[typeId].address1 == null ||
                  _actors[typeId].address1!.isEmpty) valid = false;
              if (_actors[typeId].postcode == null ||
                  _actors[typeId].postcode!.isEmpty) valid = false;
              if (_actors[typeId].location == null ||
                  _actors[typeId].location!.isEmpty) valid = false;
              if (_actors[typeId].country == null ||
                  _actors[typeId].country!.isEmpty) valid = false;
              break;
            case addTypeCombined:
              if (_actors[typeId].address1 == null ||
                  _actors[typeId].address1!.isEmpty) valid = false;
              if (_actors[typeId].address2 == null ||
                  _actors[typeId].address2!.isEmpty) valid = false;
              break;
            default:
              valid = false;
          }
        }
      } else {
        if (_actors[typeId].postcode == null ||
            _actors[typeId].postcode!.isEmpty) valid = false;
        if (_actors[typeId].location == null ||
            _actors[typeId].location!.isEmpty) valid = false;
        if (_actors[typeId].country == null ||
            _actors[typeId].country!.isEmpty) {
          valid = false;
        }
      }
    } else if (typeId == QRBill.actorCR) {
      valid = false;
    }

    if (valid && typeId > 0) {
      if (_actors[typeId].addressType == null) {
        _actors[typeId].addressType = "";
      }
      if (_actors[typeId].name == null) {
        _actors[typeId].name = "";
      }
      if (_actors[typeId].address1 == null) {
        _actors[typeId].address1 = "";
      }
      if (_actors[typeId].address2 == null) {
        _actors[typeId].address2 = "";
      }
      if (_actors[typeId].postcode == null) {
        _actors[typeId].postcode = "";
      }
      if (_actors[typeId].location == null) {
        _actors[typeId].location = "";
      }
      if (_actors[typeId].country == null) {
        _actors[typeId].country = "";
      }
    }

    return valid;
  }

  String _formatAmountAsString(double? amt) {
    if (amt == null) return "";
    int pointPos = -1;
    String amount = amt.toString();
    for (int i = 0; i < amount.length; i++) {
      if (int.tryParse(amount[i]) == null) {
        pointPos = i;
        break;
      }
    }
    String decimals = ".00";
    if (pointPos > -1) decimals = amount.substring(pointPos + 1);
    if (decimals.length > 2) {
      amount = amount.substring(0, amount.length - decimals.length + 2);
    } else if (decimals.length == 1) {
      amount += "0";
    }
    return amount.trim();
  }

  List<Data> _getStructure() {
    if (_version == null) return [];

    if (_version == 2.00) {
      return _version2;
    } else if (_version! >= 1.00) {
      return _version1;
    } else {
      _version = null;
      return [];
    }
  }
}

enum Data {
  none,
  qrType,
  version,
  coding,
  account,
  amount,
  currency,
  dueDate,
  refType,
  ref,
  crAddType,
  crName,
  crAddress1,
  crAddress2,
  crPostcode,
  crLocation,
  crCountry,
  ucrAddType,
  ucrName,
  ucrAddress1,
  ucrAddress2,
  ucrPostcode,
  urcLocation,
  ucrCountry,
  udrAddType,
  udrName,
  udrAddress1,
  udrAddress2,
  udrPostcode,
  udrLocation,
  udrCountry,
  unstrMsg,
  trailer,
  billInfo,
  altSchema1,
  altSchema2
}

class QRBillException implements Exception {
  late int _id;
  late String _msg;

  QRBillException(int id, String msg) {
    _id = id;
    _msg = msg;
  }

  int getErrorId() => _id;

  String getMessage() => toString();

  @override
  String toString() => "$runtimeType: $_msg";
}

class Actor {
  String? name = "";
  String? addressType = QRBill.addTypeStructured;
  String? address1 = "";
  String? address2 = "";
  String? postcode = "";
  String? location = "";
  String? country = "";

  late int _typeId;

  Actor(int type) {
    _typeId = type;
  }

  int getType() {
    return _typeId;
  }
}

class Modulo10 {
  static final List<List<int>> pattern = [
    [0, 9, 4, 6, 8, 2, 7, 1, 3, 5],
    [9, 4, 6, 8, 2, 7, 1, 3, 5, 0],
    [4, 6, 8, 2, 7, 1, 3, 5, 0, 9],
    [6, 8, 2, 7, 1, 3, 5, 0, 9, 4],
    [8, 2, 7, 1, 3, 5, 0, 9, 4, 6],
    [2, 7, 1, 3, 5, 0, 9, 4, 6, 8],
    [7, 1, 3, 5, 0, 9, 4, 6, 8, 2],
    [1, 3, 5, 0, 9, 4, 6, 8, 2, 7],
    [3, 5, 0, 9, 4, 6, 8, 2, 7, 1],
    [5, 0, 9, 4, 6, 8, 2, 7, 1, 3]
  ];

  static final checkDigits = [0, 9, 8, 7, 6, 5, 4, 3, 2, 1];

  static const codeLength = 27;

  static bool validate(String? input) {
    if (input != null && input.isNotEmpty) {
      input = input.replaceAll(" ", "").trim();
    }

    if (input == null) return false;

    int? check = getCheckDigit(input);
    String td = input.substring(input.length - 1);
    int? endDigit = int.tryParse(td);

    if (check == null) {
      return false;
    } else {
      return endDigit == check;
    }
  }

  static int? getCheckDigit(String input) {
    if (input.length < codeLength) return null;

    String bd = input.substring(0, input.length - 1);
    int position = 0;
    for (int i = 0; i < bd.length; i++) {
      int? digit = int.tryParse(bd[i]);
      if (digit == null) {
        return null;
      } else {
        position = pattern[position][digit];
      }
    }
    return checkDigits[position];
  }
}
