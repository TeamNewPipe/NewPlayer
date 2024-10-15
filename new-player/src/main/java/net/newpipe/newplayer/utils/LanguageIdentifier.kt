/* NewPlayer
 *
 * @author Christian Schabesberger
 *
 * Copyright (C) NewPipe e.V. 2024 <code(at)newpipe-ev.de>
 *
 * NewPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.newpipe.newplayer.utils

import android.content.res.Resources
import net.newpipe.newplayer.R

enum class LanguageIdentifier {
    AA,
    AB,
    AF,
    AK,
    AM,
    AR,
    AN,
    AS,
    AV,
    AE,
    AY,
    AZ,
    BA,
    BM,
    BE,
    BN,
    BI,
    BO,
    BS,
    BR,
    BG,
    CA,
    CS,
    CH,
    CE,
    CU,
    CV,
    KW,
    CO,
    CR,
    CY,
    DA,
    DE,
    DV,
    DZ,
    EL,
    EN,
    EO,
    ET,
    EU,
    EE,
    FO,
    FA,
    FJ,
    FI,
    FR,
    FY,
    FF,
    GD,
    GA,
    GL,
    GV,
    GN,
    GU,
    HT,
    HA,
    HE,
    HZ,
    HI,
    HO,
    HR,
    HU,
    HY,
    IG,
    IO,
    II,
    IU,
    IE,
    IA,
    ID,
    IK,
    IS,
    IT,
    JV,
    JA,
    KL,
    KN,
    KS,
    KA,
    KR,
    KK,
    KM,
    KI,
    RW,
    KY,
    KV,
    KG,
    KO,
    KJ,
    KU,
    LO,
    LA,
    LV,
    LI,
    LN,
    LT,
    LB,
    LU,
    LG,
    MH,
    ML,
    MR,
    MK,
    MG,
    MT,
    MN,
    MI,
    MS,
    MY,
    NA,
    NV,
    NR,
    ND,
    NG,
    NE,
    NL,
    NN,
    NB,
    NO,
    NY,
    OC,
    OJ,
    OR,
    OM,
    OS,
    PA,
    PI,
    PL,
    PT,
    PS,
    QU,
    RM,
    RO,
    RN,
    RU,
    SG,
    SA,
    SI,
    SK,
    SL,
    SE,
    SM,
    SN,
    SD,
    SO,
    ST,
    ES,
    SQ,
    SC,
    SR,
    SS,
    SU,
    SW,
    SV,
    TY,
    TA,
    TT,
    TE,
    TG,
    TL,
    TH,
    TI,
    TO,
    TN,
    TS,
    TK,
    TR,
    TW,
    UG,
    UK,
    UR,
    UZ,
    VE,
    VI,
    VO,
    WAL,
    WA,
    WO,
    XH,
    YI,
    YO,
    ZA,
    ZH,
    ZU;

    fun toSystemLanguageName(resources: Resources) =
        resources.getString(
            when (this) {
                AA -> R.string.aa
                AB -> R.string.ab
                AF -> R.string.af
                AK -> R.string.ak
                AM -> R.string.am
                AR -> R.string.ar
                AN -> R.string.an
                AS -> R.string.assa
                AV -> R.string.av
                AE -> R.string.ae
                AY -> R.string.ay
                AZ -> R.string.az
                BA -> R.string.ba
                BM -> R.string.bm
                BE -> R.string.be
                BN -> R.string.bn
                BI -> R.string.bi
                BO -> R.string.bo
                BS -> R.string.bs
                BR -> R.string.br
                BG -> R.string.bg
                CA -> R.string.ca
                CS -> R.string.cs
                CH -> R.string.ch
                CE -> R.string.ce
                CU -> R.string.cu
                CV -> R.string.cv
                KW -> R.string.kw
                CO -> R.string.co
                CR -> R.string.cr
                CY -> R.string.cy
                DA -> R.string.da
                DE -> R.string.de
                DV -> R.string.dv
                DZ -> R.string.dz
                EL -> R.string.el
                EN -> R.string.en
                EO -> R.string.eo
                ET -> R.string.et
                EU -> R.string.eu
                EE -> R.string.ee
                FO -> R.string.fo
                FA -> R.string.fa
                FJ -> R.string.fj
                FI -> R.string.fi
                FR -> R.string.fr
                FY -> R.string.fy
                FF -> R.string.ff
                GD -> R.string.gd
                GA -> R.string.ga
                GL -> R.string.gl
                GV -> R.string.gv
                GN -> R.string.gn
                GU -> R.string.gu
                HT -> R.string.ht
                HA -> R.string.ha
                HE -> R.string.he
                HZ -> R.string.hz
                HI -> R.string.hi
                HO -> R.string.ho
                HR -> R.string.hr
                HU -> R.string.hu
                HY -> R.string.hy
                IG -> R.string.ig
                IO -> R.string.io
                II -> R.string.ii
                IU -> R.string.iu
                IE -> R.string.ie
                IA -> R.string.ia
                ID -> R.string.id
                IK -> R.string.ik
                IS -> R.string.isl
                IT -> R.string.it
                JV -> R.string.jv
                JA -> R.string.ja
                KL -> R.string.kl
                KN -> R.string.kn
                KS -> R.string.ks
                KA -> R.string.ka
                KR -> R.string.kr
                KK -> R.string.kk
                KM -> R.string.km
                KI -> R.string.ki
                RW -> R.string.rw
                KY -> R.string.ky
                KV -> R.string.kv
                KG -> R.string.kg
                KO -> R.string.ko
                KJ -> R.string.kj
                KU -> R.string.ku
                LO -> R.string.lo
                LA -> R.string.la
                LV -> R.string.lv
                LI -> R.string.li
                LN -> R.string.ln
                LT -> R.string.lt
                LB -> R.string.lb
                LU -> R.string.lu
                LG -> R.string.lg
                MH -> R.string.mh
                ML -> R.string.ml
                MR -> R.string.mr
                MK -> R.string.mk
                MG -> R.string.mg
                MT -> R.string.mt
                MN -> R.string.mn
                MI -> R.string.mi
                MS -> R.string.ms
                MY -> R.string.my
                NA -> R.string.na
                NV -> R.string.nv
                NR -> R.string.nr
                ND -> R.string.nd
                NG -> R.string.ng
                NE -> R.string.ne
                NL -> R.string.nl
                NN -> R.string.nn
                NB -> R.string.nb
                NO -> R.string.no
                NY -> R.string.ny
                OC -> R.string.oc
                OJ -> R.string.oj
                OR -> R.string.or
                OM -> R.string.om
                OS -> R.string.os
                PA -> R.string.pa
                PI -> R.string.pi
                PL -> R.string.pl
                PT -> R.string.pt
                PS -> R.string.ps
                QU -> R.string.qu
                RM -> R.string.rm
                RO -> R.string.ro
                RN -> R.string.rn
                RU -> R.string.ru
                SG -> R.string.sg
                SA -> R.string.sa
                SI -> R.string.si
                SK -> R.string.sk
                SL -> R.string.sl
                SE -> R.string.se
                SM -> R.string.sm
                SN -> R.string.sn
                SD -> R.string.sd
                SO -> R.string.so
                ST -> R.string.st
                ES -> R.string.es
                SQ -> R.string.sq
                SC -> R.string.sc
                SR -> R.string.sr
                SS -> R.string.ss
                SU -> R.string.su
                SW -> R.string.sw
                SV -> R.string.sv
                TY -> R.string.ty
                TA -> R.string.ta
                TT -> R.string.tt
                TE -> R.string.te
                TG -> R.string.tg
                TL -> R.string.tl
                TH -> R.string.th
                TI -> R.string.ti
                TO -> R.string.to
                TN -> R.string.tn
                TS -> R.string.ts
                TK -> R.string.tk
                TR -> R.string.tr
                TW -> R.string.tw
                UG -> R.string.ug
                UK -> R.string.uk
                UR -> R.string.ur
                UZ -> R.string.uz
                VE -> R.string.ve
                VI -> R.string.vi
                VO -> R.string.vo
                WAL -> R.string.wal
                WA -> R.string.wa
                WO -> R.string.wo
                XH -> R.string.xh
                YI -> R.string.yi
                YO -> R.string.yo
                ZA -> R.string.za
                ZH -> R.string.zh
                ZU -> R.string.zu
            }
        )


    fun toEnglishName() = when (this) {
        AA -> "Afar"
        AB -> "Abkhazian"
        AF -> "Afrikaans"
        AK -> "Akan"
        AM -> "Amharic"
        AR -> "Arabic"
        AN -> "Aragonese"
        AS -> "Assamese"
        AV -> "Avaric"
        AE -> "Avestan"
        AY -> "Aymara"
        AZ -> "Azerbaijani"
        BA -> "Bashkir"
        BM -> "Bambara"
        BE -> "Belarusian"
        BN -> "Bengali"
        BI -> "Bislama"
        BO -> "Tibetan"
        BS -> "Bosnian"
        BR -> "Breton"
        BG -> "Bulgarian"
        CA -> "Catalan; Valencian"
        CS -> "Czech"
        CH -> "Chamorro"
        CE -> "Chechen"
        CU -> "Church Slavic; Old Slavonic; Church Slavonic; Old Bulgarian; Old Church Slavonic"
        CV -> "Chuvash"
        KW -> "Cornish"
        CO -> "Corsican"
        CR -> "Cree"
        CY -> "Welsh"
        DA -> "Danish"
        DE -> "German"
        DV -> "Divehi; Dhivehi; Maldivian"
        DZ -> "Dzongkha"
        EL -> "Greek, Modern (1453–)"
        EN -> "English"
        EO -> "Esperanto"
        ET -> "Estonian"
        EU -> "Basque"
        EE -> "Ewe"
        FO -> "Faroese"
        FA -> "Persian"
        FJ -> "Fijian"
        FI -> "Finnish"
        FR -> "French"
        FY -> "Western Frisian"
        FF -> "Fulah"
        GD -> "Gaelic; Scottish Gaelic"
        GA -> "Irish"
        GL -> "Galician"
        GV -> "Manx"
        GN -> "Guarani"
        GU -> "Gujarati"
        HT -> "Haitian; Haitian Creole"
        HA -> "Hausa"
        HE -> "Hebrew"
        HZ -> "Herero"
        HI -> "Hindi"
        HO -> "Hiri Motu"
        HR -> "Croatian"
        HU -> "Hungarian"
        HY -> "Armenian"
        IG -> "Igbo"
        IO -> "Ido"
        II -> "Sichuan Yi; Nuosu"
        IU -> "Inuktitut"
        IE -> "Interlingue; Occidental"
        IA -> "Interlingua"
        ID -> "Indonesian"
        IK -> "Inupiaq"
        IS -> "Icelandic"
        IT -> "Italian"
        JV -> "Javanese"
        JA -> "Japanese"
        KL -> "Kalaallisut; Greenlandic"
        KN -> "Kannada"
        KS -> "Kashmiri"
        KA -> "Georgian"
        KR -> "Kanuri"
        KK -> "Kazakh"
        KM -> "Central Khmer"
        KI -> "Kikuyu; Gikuyu"
        RW -> "Kinyarwanda"
        KY -> "Kirghiz; Kyrgyz"
        KV -> "Komi"
        KG -> "Kongo"
        KO -> "Korean"
        KJ -> "Kuanyama; Kwanyama"
        KU -> "Kurdish"
        LO -> "Lao"
        LA -> "Latin"
        LV -> "Latvian"
        LI -> "Limburgan; Limburger; Limburgish"
        LN -> "Lingala"
        LT -> "Lithuanian"
        LB -> "Luxembourgish; Letzeburgesch"
        LU -> "Luba-Katanga"
        LG -> "Ganda"
        MH -> "Marshallese"
        ML -> "Malayalam"
        MR -> "Marathi"
        MK -> "Macedonian"
        MG -> "Malagasy"
        MT -> "Maltese"
        MN -> "Mongolian"
        MI -> "Māori"
        MS -> "Malay"
        MY -> "Burmese"
        NA -> "Nauru"
        NV -> "Navajo; Navaho"
        NR -> "Ndebele, South; South Ndebele"
        ND -> "Ndebele, North; North Ndebele"
        NG -> "Ndonga"
        NE -> "Nepali"
        NL -> "Dutch; Flemish"
        NN -> "Norwegian Nynorsk; Nynorsk, Norwegian"
        NB -> "Bokmål, Norwegian; Norwegian Bokmål"
        NO -> "Norwegian"
        NY -> "Chichewa; Chewa; Nyanja"
        OC -> "Occitan (post 1500)"
        OJ -> "Ojibwa"
        OR -> "Oriya"
        OM -> "Oromo"
        OS -> "Ossetian; Ossetic"
        PA -> "Panjabi; Punjabi"
        PI -> "Pali"
        PL -> "Polish"
        PT -> "Portuguese"
        PS -> "Pushto; Pashto"
        QU -> "Quechua"
        RM -> "Romansh"
        RO -> "Romanian; Moldavian; Moldovan"
        RN -> "Rundi"
        RU -> "Russian"
        SG -> "Sango"
        SA -> "Sanskrit"
        SI -> "Sinhala; Sinhalese"
        SK -> "Slovak"
        SL -> "Slovenian"
        SE -> "Northern Sami"
        SM -> "Samoan"
        SN -> "Shona"
        SD -> "Sindhi"
        SO -> "Somali"
        ST -> "Sotho, Southern; Southern Sotho"
        ES -> "Spanish; Castilian"
        SQ -> "Albanian"
        SC -> "Sardinian"
        SR -> "Serbian"
        SS -> "Swati"
        SU -> "Sundanese"
        SW -> "Swahili"
        SV -> "Swedish"
        TY -> "Tahitian"
        TA -> "Tamil"
        TT -> "Tatar"
        TE -> "Telugu"
        TG -> "Tajik"
        TL -> "Tagalog"
        TH -> "Thai"
        TI -> "Tigrinya"
        TO -> "Tonga (Tonga Islands)"
        TN -> "Tswana"
        TS -> "Tsonga"
        TK -> "Turkmen"
        TR -> "Turkish"
        TW -> "Twi"
        UG -> "Uighur; Uyghur"
        UK -> "Ukrainian"
        UR -> "Urdu"
        UZ -> "Uzbek"
        VE -> "Venda"
        VI -> "Vietnamese"
        VO -> "Volapük"
        WAL -> "wal,Wolayttattuwa"
        WA -> "Walloon"
        WO -> "Wolof"
        XH -> "Xhosa"
        YI -> "Yiddish"
        YO -> "Yoruba"
        ZA -> "Zhuang; Chuang"
        ZH -> "Chinese"
        ZU -> "Zulu"
    }

    fun toNativeLanguageName() =
        when (this) {
            AA -> "Qafaraf; ’Afar Af; Afaraf; Qafar af"
            AB -> "Аҧсуа бызшәа Aƥsua bızšwa; Аҧсшәа Aƥsua"
            AF -> "Afrikaans"
            AK -> "Akan"
            AM -> "አማርኛ Amârıñâ"
            AR -> "العربية; al'Arabiyyeẗ"
            AN -> "aragonés"
            AS -> "অসমীয়া"
            AV -> "Магӏарул мацӏ; Авар мацӏ"
            AE -> toEnglishName()
            AY -> "Aymar aru"
            AZ -> "Azərbaycan dili; آذربایجان دیلی; Азәрбајҹан дили"
            BA -> "Башҡорт теле; Başqort tele"
            BM -> "ߓߊߡߊߣߊߣߞߊߣ"
            BE -> "Беларуская мова Belaruskaâ mova"
            BN -> "বাংলা Bāŋlā"
            BI -> toEnglishName()
            BO -> "བོད་སྐད་ Bodskad; ལྷ་སའི་སྐད་ Lhas'iskad"
            BS -> "bosanski"
            BR -> "Brezhoneg"
            BG -> "български език bălgarski ezik"
            CA -> "valencià"
            CS -> "čeština; český jazyk"
            CH -> "Finu' Chamoru"
            CE -> "Нохчийн мотт; نَاخچیین موٓتت; ნახჩიე მუოთთ"
            CU -> "Славе́нскїй ѧ҆зы́къ"
            CV -> "Чӑвашла"
            KW -> "Kernowek"
            CO -> "Corsu; Lingua corsa"
            CR -> toEnglishName()
            CY -> "Cymraeg; y Gymraeg"
            DA -> "dansk"
            DE -> "Deutsch"
            DV -> "ދިވެހި; ދިވެހިބަސް Divehi"
            DZ -> "རྫོང་ཁ་ Ĵoŋkha"
            EL -> "Νέα Ελληνικά Néa Ellêniká"
            EN -> "English"
            EO -> "Esperanto"
            ET -> "eesti keel"
            EU -> "euskara"
            EE -> "Èʋegbe"
            FO -> "føroyskt"
            FA -> "فارسی Fārsiy"
            FJ -> "Na Vosa Vakaviti"
            FI -> "suomen kieli"
            FR -> "français"
            FY -> "Frysk"
            FF -> "Fulfulde; Pulaar; Pular"
            GD -> "Gàidhlig"
            GA -> "Gaeilge"
            GL -> "galego"
            GV -> "Gaelg; Gailck"
            GN -> "Avañe'ẽ"
            GU -> "ગુજરાતી Gujarātī"
            HT -> "kreyòl ayisyen"
            HA -> "Harshen Hausa; هَرْشَن"
            HE -> "עברית 'Ivriyþ"
            HZ -> "Otjiherero"
            HI -> "हिन्दी Hindī"
            HO -> toEnglishName()
            HR -> "hrvatski"
            HU -> "magyar nyelv"
            HY -> "Հայերէն Hayerèn; Հայերեն Hayeren"
            IG -> "Asụsụ Igbo"
            IO -> toEnglishName()
            II -> "ꆈꌠꉙ Nuosuhxop"
            IU -> "ᐃᓄᒃᑎᑐᑦ Inuktitut"
            IE -> toEnglishName()
            IA -> toEnglishName()
            ID -> "bahasa Indonesia"
            IK -> "Iñupiaq"
            IS -> "íslenska"
            IT -> "italiano; lingua italiana"
            JV -> "ꦧꦱꦗꦮ / Basa Jawa"
            JA -> "日本語 Nihongo"
            KL -> toEnglishName()
            KN -> "ಕನ್ನಡ Kannađa"
            KS -> "कॉशुर / كأشُر"
            KA -> "ქართული Kharthuli"
            KR -> toEnglishName()
            KK -> "қазақ тілі qazaq tili; қазақша qazaqşa"
            KM -> "ភាសាខ្មែរ Phiəsaakhmær"
            KI -> "Gĩkũyũ"
            RW -> "Ikinyarwanda"
            KY -> "кыргызча kırgızça; кыргыз тили kırgız tili"
            KV -> "Коми кыв"
            KG -> toEnglishName()
            KO -> "한국어 Han'gug'ô"
            KJ -> toEnglishName()
            KU -> "kurdî / کوردی"
            LO -> "ພາສາລາວ Phasalaw"
            LA -> "Lingua latīna"
            LV -> "Latviešu valoda"
            LI -> "Lèmburgs"
            LN -> toEnglishName()
            LT -> "lietuvių kalba"
            LB -> "Lëtzebuergesch"
            LU -> "Kiluba"
            LG -> "Luganda"
            MH -> "Kajin M̧ajeļ"
            ML -> "മലയാളം Malayāļã"
            MR -> "मराठी Marāţhī"
            MK -> "македонски јазик makedonski jazik"
            MG -> toEnglishName()
            MT -> "Malti"
            MN -> "монгол хэл (mongol xel; ᠮᠣᠩᠭᠣᠯ ᠬᠡᠯᠡ"
            MI -> "Te Reo Māori"
            MS -> "Bahasa Melayu"
            MY -> "မြန်မာစာ Mrãmācā; မြန်မာစကား Mrãmākā:"
            NA -> "dorerin Naoero"
            NV -> "Diné bizaad; Naabeehó bizaad"
            NR -> "isiNdebele seSewula"
            ND -> "siNdebele saseNyakatho"
            NG -> "ndonga"
            NE -> "नेपाली भाषा Nepālī bhāśā"
            NL -> "Nederlands; Vlaams"
            NN -> "norsk nynorsk"
            NB -> "norsk bokmål"
            NO -> "norsk"
            NY -> "Chichewa; Chinyanja"
            OC -> "occitan; lenga d'òc"
            OJ -> toEnglishName()
            OR -> "ଓଡ଼ିଆ"
            OM -> "Afaan Oromoo"
            OS -> "Ирон ӕвзаг Iron ævzag"
            PA -> "ਪੰਜਾਬੀ / پنجابی Pãjābī"
            PI -> "Pāli"
            PL -> "Język polski"
            PT -> "português"
            PS -> "پښتو Pax̌tow"
            QU -> "Runa simi; kichwa simi; Nuna shimi"
            RM -> "Rumantsch; Rumàntsch; Romauntsch; Romontsch"
            RO -> "limba română"
            RN -> "Ikirundi"
            RU -> "русский язык russkiĭ âzyk"
            SG -> "yângâ tî sängö"
            SA -> "संस्कृतम् Sąskŕtam; 𑌸𑌂𑌸𑍍𑌕𑍃𑌤𑌮𑍍"
            SI -> "සිංහල Sĩhala"
            SK -> "slovenčina; slovenský jazyk"
            SL -> "slovenski jezik; slovenščina"
            SE -> "davvisámegiella"
            SM -> "Gagana faʻa Sāmoa"
            SN -> "chiShona"
            SD -> "سنڌي / सिन्धी / ਸਿੰਧੀ"
            SO -> "af Soomaali"
            ST -> "Sesotho; Sesotho sa Borwa"
            ES -> "español; castellano"
            SQ -> "Shqip"
            SC -> "sardu; limba sarda; lingua sarda"
            SR -> "српски / srpski"
            SS -> "siSwati"
            SU -> "ᮘᮞ ᮞᮥᮔ᮪ᮓ / Basa Sunda"
            SW -> "Kiswahili"
            SV -> "svenska"
            TY -> "Reo Tahiti; Reo Mā'ohi"
            TA -> "தமிழ் Tamił"
            TT -> "татар теле / tatar tele / تاتار"
            TE -> "తెలుగు Telugu"
            TG -> "тоҷикӣ toçikī"
            TL -> "Wikang Tagalog"
            TH -> "ภาษาไทย Phasathay"
            TI -> "ትግርኛ"
            TO -> "lea faka-Tonga"
            TN -> "Setswana"
            TS -> "Xitsonga"
            TK -> "Türkmençe / Түркменче / تورکمن تیلی تورکمنچ; türkmen dili / түркмен дили"
            TR -> "Türkçe"
            TW -> toEnglishName()
            UG -> "ئۇيغۇرچە  ;  ئۇيغۇر تىلى"
            UK -> "Українська мова; Українська"
            UR -> "اُردُو Urduw"
            UZ -> "Oʻzbekcha / Ózbekça / ўзбекча / ئوزبېچه; oʻzbek tili / ўзбек тили /  ئوبېک تیلی"
            VE -> "Tshivenḓa"
            VI -> "Tiếng Việt"
            VO -> toEnglishName()
            WAL -> "Wolayttattuwa"
            WA -> "Walon"
            WO -> toEnglishName()
            XH -> "isiXhosa"
            YI -> "ייִדיש; יידיש; אידיש Yidiš"
            YO -> "èdè Yorùbá"
            ZA -> "Vahcuengh / 話僮"
            ZH -> "中文; Zhōngwén; 汉语; 漢語; Hànyǔ"
            ZU -> "isiZulu"
        }


    fun toISO639_1() =
        when (this) {
            AA -> "aa"
            AB -> "ab"
            AF -> "af"
            AK -> "ak"
            AM -> "am"
            AR -> "ar"
            AN -> "an"
            AS -> "as"
            AV -> "av"
            AE -> "ae"
            AY -> "ay"
            AZ -> "az"
            BA -> "ba"
            BM -> "bm"
            BE -> "be"
            BN -> "bn"
            BI -> "bi"
            BO -> "bo"
            BS -> "bs"
            BR -> "br"
            BG -> "bg"
            CA -> "ca"
            CS -> "cs"
            CH -> "ch"
            CE -> "ce"
            CU -> "cu"
            CV -> "cv"
            KW -> "kw"
            CO -> "co"
            CR -> "cr"
            CY -> "cy"
            DA -> "da"
            DE -> "de"
            DV -> "dv"
            DZ -> "dz"
            EL -> "el"
            EN -> "en"
            EO -> "eo"
            ET -> "et"
            EU -> "eu"
            EE -> "ee"
            FO -> "fo"
            FA -> "fa"
            FJ -> "fj"
            FI -> "fi"
            FR -> "fr"
            FY -> "fy"
            FF -> "ff"
            GD -> "gd"
            GA -> "ga"
            GL -> "gl"
            GV -> "gv"
            GN -> "gn"
            GU -> "gu"
            HT -> "ht"
            HA -> "ha"
            HE -> "he"
            HZ -> "hz"
            HI -> "hi"
            HO -> "ho"
            HR -> "hr"
            HU -> "hu"
            HY -> "hy"
            IG -> "ig"
            IO -> "io"
            II -> "ii"
            IU -> "iu"
            IE -> "ie"
            IA -> "ia"
            ID -> "id"
            IK -> "ik"
            IS -> "is"
            IT -> "it"
            JV -> "jv"
            JA -> "ja"
            KL -> "kl"
            KN -> "kn"
            KS -> "ks"
            KA -> "ka"
            KR -> "kr"
            KK -> "kk"
            KM -> "km"
            KI -> "ki"
            RW -> "rw"
            KY -> "ky"
            KV -> "kv"
            KG -> "kg"
            KO -> "ko"
            KJ -> "kj"
            KU -> "ku"
            LO -> "lo"
            LA -> "la"
            LV -> "lv"
            LI -> "li"
            LN -> "ln"
            LT -> "lt"
            LB -> "lb"
            LU -> "lu"
            LG -> "lg"
            MH -> "mh"
            ML -> "ml"
            MR -> "mr"
            MK -> "mk"
            MG -> "mg"
            MT -> "mt"
            MN -> "mn"
            MI -> "mi"
            MS -> "ms"
            MY -> "my"
            NA -> "na"
            NV -> "nv"
            NR -> "nr"
            ND -> "nd"
            NG -> "ng"
            NE -> "ne"
            NL -> "nl"
            NN -> "nn"
            NB -> "nb"
            NO -> "no"
            NY -> "ny"
            OC -> "oc"
            OJ -> "oj"
            OR -> "or"
            OM -> "om"
            OS -> "os"
            PA -> "pa"
            PI -> "pi"
            PL -> "pl"
            PT -> "pt"
            PS -> "ps"
            QU -> "qu"
            RM -> "rm"
            RO -> "ro"
            RN -> "rn"
            RU -> "ru"
            SG -> "sg"
            SA -> "sa"
            SI -> "si"
            SK -> "sk"
            SL -> "sl"
            SE -> "se"
            SM -> "sm"
            SN -> "sn"
            SD -> "sd"
            SO -> "so"
            ST -> "st"
            ES -> "es"
            SQ -> "sq"
            SC -> "sc"
            SR -> "sr"
            SS -> "ss"
            SU -> "su"
            SW -> "sw"
            SV -> "sv"
            TY -> "ty"
            TA -> "ta"
            TT -> "tt"
            TE -> "te"
            TG -> "tg"
            TL -> "tl"
            TH -> "th"
            TI -> "ti"
            TO -> "to"
            TN -> "tn"
            TS -> "ts"
            TK -> "tk"
            TR -> "tr"
            TW -> "tw"
            UG -> "ug"
            UK -> "uk"
            UR -> "ur"
            UZ -> "uz"
            VE -> "ve"
            VI -> "vi"
            VO -> "vo"
            WAL -> "wal"
            WA -> "wa"
            WO -> "wo"
            XH -> "xh"
            YI -> "yi"
            YO -> "yo"
            ZA -> "za"
            ZH -> "zh"
            ZU -> "zu"
        }


    companion object {
        fun fromISO639_1(identifier: String) =
            when (identifier) {
                "aa" -> AA
                "ab" -> AB
                "af" -> AF
                "ak" -> AK
                "am" -> AM
                "ar" -> AR
                "an" -> AN
                "as" -> AS
                "av" -> AV
                "ae" -> AE
                "ay" -> AY
                "az" -> AZ
                "ba" -> BA
                "bm" -> BM
                "be" -> BE
                "bn" -> BN
                "bi" -> BI
                "bo" -> BO
                "bs" -> BS
                "br" -> BR
                "bg" -> BG
                "ca" -> CA
                "cs" -> CS
                "ch" -> CH
                "ce" -> CE
                "cu" -> CU
                "cv" -> CV
                "kw" -> KW
                "co" -> CO
                "cr" -> CR
                "cy" -> CY
                "da" -> DA
                "de" -> DE
                "dv" -> DV
                "dz" -> DZ
                "el" -> EL
                "en" -> EN
                "eo" -> EO
                "et" -> ET
                "eu" -> EU
                "ee" -> EE
                "fo" -> FO
                "fa" -> FA
                "fj" -> FJ
                "fi" -> FI
                "fr" -> FR
                "fy" -> FY
                "ff" -> FF
                "gd" -> GD
                "ga" -> GA
                "gl" -> GL
                "gv" -> GV
                "gn" -> GN
                "gu" -> GU
                "ht" -> HT
                "ha" -> HA
                "he" -> HE
                "hz" -> HZ
                "hi" -> HI
                "ho" -> HO
                "hr" -> HR
                "hu" -> HU
                "hy" -> HY
                "ig" -> IG
                "io" -> IO
                "ii" -> II
                "iu" -> IU
                "ie" -> IE
                "ia" -> IA
                "id" -> ID
                "ik" -> IK
                "is" -> IS
                "it" -> IT
                "jv" -> JV
                "ja" -> JA
                "kl" -> KL
                "kn" -> KN
                "ks" -> KS
                "ka" -> KA
                "kr" -> KR
                "kk" -> KK
                "km" -> KM
                "ki" -> KI
                "rw" -> RW
                "ky" -> KY
                "kv" -> KV
                "kg" -> KG
                "ko" -> KO
                "kj" -> KJ
                "ku" -> KU
                "lo" -> LO
                "la" -> LA
                "lv" -> LV
                "li" -> LI
                "ln" -> LN
                "lt" -> LT
                "lb" -> LB
                "lu" -> LU
                "lg" -> LG
                "mh" -> MH
                "ml" -> ML
                "mr" -> MR
                "mk" -> MK
                "mg" -> MG
                "mt" -> MT
                "mn" -> MN
                "mi" -> MI
                "ms" -> MS
                "my" -> MY
                "na" -> NA
                "nv" -> NV
                "nr" -> NR
                "nd" -> ND
                "ng" -> NG
                "ne" -> NE
                "nl" -> NL
                "nn" -> NN
                "nb" -> NB
                "no" -> NO
                "ny" -> NY
                "oc" -> OC
                "oj" -> OJ
                "or" -> OR
                "om" -> OM
                "os" -> OS
                "pa" -> PA
                "pi" -> PI
                "pl" -> PL
                "pt" -> PT
                "ps" -> PS
                "qu" -> QU
                "rm" -> RM
                "ro" -> RO
                "rn" -> RN
                "ru" -> RU
                "sg" -> SG
                "sa" -> SA
                "si" -> SI
                "sk" -> SK
                "sl" -> SL
                "se" -> SE
                "sm" -> SM
                "sn" -> SN
                "sd" -> SD
                "so" -> SO
                "st" -> ST
                "es" -> ES
                "sq" -> SQ
                "sc" -> SC
                "sr" -> SR
                "ss" -> SS
                "su" -> SU
                "sw" -> SW
                "sv" -> SV
                "ty" -> TY
                "ta" -> TA
                "tt" -> TT
                "te" -> TE
                "tg" -> TG
                "tl" -> TL
                "th" -> TH
                "ti" -> TI
                "to" -> TO
                "tn" -> TN
                "ts" -> TS
                "tk" -> TK
                "tr" -> TR
                "tw" -> TW
                "ug" -> UG
                "uk" -> UK
                "ur" -> UR
                "uz" -> UZ
                "ve" -> VE
                "vi" -> VI
                "vo" -> VO
                "wal" -> WAL
                "wa" -> WA
                "wo" -> WO
                "xh" -> XH
                "yi" -> YI
                "yo" -> YO
                "za" -> ZA
                "zh" -> ZH
                "zu" -> ZU
                else -> throw NewPlayerException("Unknwon language identifier: $identifier")
            }

        fun fromISO639_3(lang: String) =
            when(lang) {
                "aar" -> 	AA
                "abk" -> 	AB
                "afr" -> 	AF
                "aka" -> 	AK
                "amh" -> 	AM
                "ara" -> 	AR
                "arg" -> 	AN
                "asm" -> 	AS
                "ava" -> 	AV
                "ave" -> 	AE
                "aym" -> 	AY
                "aze" -> 	AZ
                "bak" -> 	BA
                "bam" -> 	BM
                "bel" -> 	BE
                "ben" -> 	BN
                "bis" -> 	BI
                "bod" -> 	BO
                "bos" -> 	BS
                "bre" -> 	BR
                "bul" -> 	BG
                "cat" -> 	CA
                "ces" -> 	CS
                "cha" -> 	CH
                "che" -> 	CE
                "chu" -> 	CU
                "chv" -> 	CV
                "cor" -> 	KW
                "cos" -> 	CO
                "cre" -> 	CR
                "cym" -> 	CY
                "dan" -> 	DA
                "deu" -> 	DE
                "div" -> 	DV
                "dzo" -> 	DZ
                "ell" -> 	EL
                "eng" -> 	EN
                "epo" -> 	EO
                "est" -> 	ET
                "eus" -> 	EU
                "ewe" -> 	EE
                "fao" -> 	FO
                "fas" -> 	FA
                "fij" -> 	FJ
                "fin" -> 	FI
                "fra" -> 	FR
                "fry" -> 	FY
                "ful" -> 	FF
                "gla" -> 	GD
                "gle" -> 	GA
                "glg" -> 	GL
                "glv" -> 	GV
                "grn" -> 	GN
                "guj" -> 	GU
                "hat" -> 	HT
                "hau" -> 	HA
                "heb" -> 	HE
                "her" -> 	HZ
                "hin" -> 	HI
                "hmo" -> 	HO
                "hrv" -> 	HR
                "hun" -> 	HU
                "hye" -> 	HY
                "ibo" -> 	IG
                "ido" -> 	IO
                "iii" -> 	II
                "iku" -> 	IU
                "ile" -> 	IE
                "ina" -> 	IA
                "ind" -> 	ID
                "ipk" -> 	IK
                "isl" -> 	IS
                "ita" -> 	IT
                "jav" -> 	JV
                "jpn" -> 	JA
                "kal" -> 	KL
                "kan" -> 	KN
                "kas" -> 	KS
                "kat" -> 	KA
                "kau" -> 	KR
                "kaz" -> 	KK
                "khm" -> 	KM
                "kik" -> 	KI
                "kin" -> 	RW
                "kir" -> 	KY
                "kom" -> 	KV
                "kon" -> 	KG
                "kor" -> 	KO
                "kua" -> 	KJ
                "kur" -> 	KU
                "lao" -> 	LO
                "lat" -> 	LA
                "lav" -> 	LV
                "lim" -> 	LI
                "lin" -> 	LN
                "lit" -> 	LT
                "ltz" -> 	LB
                "lub" -> 	LU
                "lug" -> 	LG
                "mah" -> 	MH
                "mal" -> 	ML
                "mar" -> 	MR
                "mkd" -> 	MK
                "mlg" -> 	MG
                "mlt" -> 	MT
                "mon" -> 	MN
                "mri" -> 	MI
                "msa" -> 	MS
                "mya" -> 	MY
                "nau" -> 	NA
                "nav" -> 	NV
                "nbl" -> 	NR
                "nde" -> 	ND
                "ndo" -> 	NG
                "nep" -> 	NE
                "nld" -> 	NL
                "nno" -> 	NN
                "nob" -> 	NB
                "nor" -> 	NO
                "nya" -> 	NY
                "oci" -> 	OC
                "oji" -> 	OJ
                "ori" -> 	OR
                "orm" -> 	OM
                "oss" -> 	OS
                "pan" -> 	PA
                "pli" -> 	PI
                "pol" -> 	PL
                "por" -> 	PT
                "pus" -> 	PS
                "que" -> 	QU
                "roh" -> 	RM
                "ron" -> 	RO
                "run" -> 	RN
                "rus" -> 	RU
                "sag" -> 	SG
                "san" -> 	SA
                "sin" -> 	SI
                "slk" -> 	SK
                "slv" -> 	SL
                "sme" -> 	SE
                "smo" -> 	SM
                "sna" -> 	SN
                "snd" -> 	SD
                "som" -> 	SO
                "sot" -> 	ST
                "spa" -> 	ES
                "sqi" -> 	SQ
                "srd" -> 	SC
                "srp" -> 	SR
                "ssw" -> 	SS
                "sun" -> 	SU
                "swa" -> 	SW
                "swe" -> 	SV
                "tah" -> 	TY
                "tam" -> 	TA
                "tat" -> 	TT
                "tel" -> 	TE
                "tgk" -> 	TG
                "tgl" -> 	TL
                "tha" -> 	TH
                "tir" -> 	TI
                "ton" -> 	TO
                "tsn" -> 	TN
                "tso" -> 	TS
                "tuk" -> 	TK
                "tur" -> 	TR
                "twi" -> 	TW
                "uig" -> 	UG
                "ukr" -> 	UK
                "urd" -> 	UR
                "uzb" -> 	UZ
                "ven" -> 	VE
                "vie" -> 	VI
                "vol" -> 	VO
                "wal" -> 	WAL
                "wln" -> 	WA
                "wol" -> 	WO
                "xho" -> 	XH
                "yid" -> 	YI
                "yor" -> 	YO
                "zha" -> 	ZA
                "zho" -> 	ZH
                "zul" -> 	ZU
                else -> throw NewPlayerException("Unknwon language in ISO639_3 format: $lang")
            }

    }
}
