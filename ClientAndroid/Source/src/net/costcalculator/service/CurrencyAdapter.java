/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Adapter provides access to all supported currencies
 * 
 * <pre>
 * Usage:
 * {
 *     // get currencies adapter
 *     CurrencyAdapter.getAdapter();
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class CurrencyAdapter
{
    public static ArrayAdapter<String> getAdapter(Context c)
    {
        String[] currencies = getCurrencies();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,
                android.R.layout.simple_list_item_1, currencies);

        return adapter;
    }

    private static String[] getCurrencies()
    {
        String[] c = new String[]
        {
                "AED - United Arab Emirates dirham",
                "AFN - Afghani",
                "ALL - Lek",
                "AMD - Armenian Dram",
                "ANG - Netherlands Antillian Guilder",
                "AOA - Kwanza",
                "ARS - Argentine Peso",
                "AUD - Australian Dollar",
                "AWG - Aruban Guilder",
                "AZN - Azerbaijanian Manat",
                "BAM - Convertible Marks",
                "BBD - Barbados Dollar",
                "BDT - Bangladeshi Taka",
                "BGN - Bulgarian Lev",
                "BHD - Bahraini Dinar",
                "BIF - Burundian Franc",
                "BMD - Bermudian Dollar",
                "BND - Brunei Dollar",
                "BOB - Boliviano",
                "BOV - Bolivian Mvdol",
                "BRL - Brazilian Real",
                "BSD - Bahamian Dollar",
                "BTN - Ngultrum",
                "BWP - Pula",
                "BYR - Belarussian Ruble",
                "BZD - Belize Dollar",
                "CAD - Canadian Dollar",
                "CDF - Franc Congolais",
                "CHE - WIR Euro",
                "CHF - Swiss Franc",
                "CHW - WIR Franc",
                "CLF - Unidades de formento",
                "CLP - Chilean Peso",
                "CNY - Yuan Renminbi",
                "COP - Colombian Peso",
                "COU - Unidad de Valor Real",
                "CRC - Costa Rican Colon",
                "CUP - Cuban Peso",
                "CVE - Cape Verde Escudo",
                "CYP - Cyprus Pound",
                "CZK - Czech Koruna",
                "DJF - Djibouti Franc",
                "DKK - Danish Krone",
                "DOP - Dominican Peso",
                "DZD - Algerian Dinar",
                "EEK - Kroon",
                "EGP - Egyptian Pound",
                "ERN - Nakfa",
                "ETB - Ethiopian Birr",
                "EUR - Euro",
                "FJD - Fiji Dollar",
                "FKP - Falkland Islands Pound",
                "GBP - Pound Sterling",
                "GEL - Lari",
                "GHC - Cedi",
                "GIP - Gibraltar pound",
                "GMD - Dalasi",
                "GNF - Guinea Franc",
                "GTQ - Quetzal",
                "GYD - Guyana Dollar",
                "HKD - Hong Kong Dollar",
                "HNL - Lempira",
                "HRK - Croatian Kuna",
                "HTG - Haiti Gourde",
                "HUF - Forint",
                "IDR - Rupiah",
                "ILS - New Israeli Shekel",
                "INR - Indian Rupee",
                "IQD - Iraqi Dinar",
                "IRR - Iranian Rial",
                "ISK - Iceland Krona",
                "JMD - Jamaican Dollar",
                "JOD - Jordanian Dinar",
                "JPY - Japanese yen",
                "KES - Kenyan Shilling",
                "KGS - Som",
                "KHR - Riel",
                "KMF - Comoro Franc",
                "KPW - North Korean Won",
                "KRW - South Korean Won",
                "KWD - Kuwaiti Dinar",
                "KYD - Cayman Islands Dollar",
                "KZT - Tenge",
                "LAK - Kip",
                "LBP - Lebanese Pound",
                "LKR - Sri Lanka Rupee",
                "LRD - Liberian Dollar",
                "LSL - Loti",
                "LTL - Lithuanian Litas",
                "LVL - Latvian Lats",
                "LYD - Libyan Dinar",
                "MAD - Moroccan Dirham",
                "MDL - Moldovan Leu",
                "MGA - Malagasy Ariary",
                "MKD - Denar",
                "MMK - Kyat",
                "MNT - Tugrik",
                "MOP - Pataca",
                "MRO - Ouguiya",
                "MTL - Maltese Lira",
                "MUR - Mauritius Rupee",
                "MVR - Rufiyaa",
                "MWK - Kwacha",
                "MXN - Mexican Peso",
                "MXV - Mexican Unidad de Inversion",
                "MYR - Malaysian Ringgit",
                "MZN - Metical",
                "NAD - Namibian Dollar",
                "NGN - Naira",
                "NIO - Cordoba Oro",
                "NOK - Norwegian Krone",
                "NPR - Nepalese Rupee",
                "NZD - New Zealand Dollar",
                "OMR - Rial Omani",
                "PAB - Balboa",
                "PEN - Nuevo Sol",
                "PGK - Kina",
                "PHP - Philippine Peso",
                "PKR - Pakistan Rupee",
                "PLN - Zloty",
                "PYG - Guarani",
                "QAR - Qatari Rial",
                "ROL - Romanian Leu",
                "RON - Romanian New Leu",
                "RSD - Serbian Dinar",
                "RUB - Russian Ruble",
                "RWF - Rwanda Franc",
                "SAR - Saudi Riyal",
                "SBD - Solomon Islands Dollar",
                "SCR - Seychelles Rupee",
                "SDD - Sudanese Dinar",
                "SDG - Sudanese Pound",
                "SEK - Swedish Krona",
                "SGD - Singapore Dollar",
                "SHP - Saint Helena Pound",
                "SKK - Slovak Koruna",
                "SLL - Leone",
                "SOS - Somali Shilling",
                "SRD - Surinam Dollar",
                "STD - Dobra",
                "SYP - Syrian Pound",
                "SZL - Lilangeni",
                "THB - Baht",
                "TJS - Somoni",
                "TMM - Manat",
                "TND - Tunisian Dinar",
                "TOP - Pa'anga",
                "TRY - New Turkish Lira",
                "TTD - Trinidad and Tobago Dollar",
                "TWD - New Taiwan Dollar",
                "TZS - Tanzanian Shilling",
                "UAH - Hryvnia",
                "UGX - Uganda Shilling",
                "USD - US Dollar",
                "UYU - Peso Uruguayo",
                "UZS - Uzbekistan Som",
                "VEB - Venezuelan bolA�var",
                "VND - Vietnamese đồng",
                "VUV - Vatu",
                "WST - Samoan Tala",
                "XAF - CFA Franc BEAC",
                "XCD - East Caribbean Dollar",
                "XOF - CFA Franc BCEAO",
                "XPF - CFP franc",
                "YER - Yemeni Rial",
                "ZAR - South African Rand",
                "ZMK - Kwacha",
                "ZWD - Zimbabwe Dollar"
        };
        
        return c;
    }
}
