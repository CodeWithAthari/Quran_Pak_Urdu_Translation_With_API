package com.atriiapps.quranpakinurdu.Utilities;

public class Constants {
    public String QURAN_TRANSLATION_VERSION = "ur_maududi";
    public String WEBSITE_BASE_URL = "https://atharapps.com/quran/";
    public String BASE_URL = WEBSITE_BASE_URL + "?v=" + QURAN_TRANSLATION_VERSION + "&q=";
    public String GET_AYA = BASE_URL + "get_aya&aya_no=";
    public String GET_PAGE = BASE_URL + "get_page&page_no=";
    public String GET_RANDOM_AYA_QURAN = BASE_URL + "get_random";
    public String GET_SURA = BASE_URL + "get_sura&sura_no=";
    public String GET_SURA_META = BASE_URL + "get_sura_meta";
    public String GET_SURA_META_ONE = BASE_URL + "get_sura_meta_one&sura_no=";
    public String GET_AYA_FROM_SURA = BASE_URL + "get_aya_from_sura"; // 2 PARAMS REQUIRED sura_no & aya_no

    public final int TOTAL_AYA_IN_QURAN = 6236;
    public final int TOTAL_SURA_IN_QURAN = 114;

    public final String DEFAULT_QURAN_TRANSLATION_VERSION_SHARED_PREF = "ur_maududi";


   public void updateConstants() {
       BASE_URL = WEBSITE_BASE_URL + "?v=" + QURAN_TRANSLATION_VERSION + "&q=";
        GET_SURA = BASE_URL + "get_sura&sura_no=";
    }


}
