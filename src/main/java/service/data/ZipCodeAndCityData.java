package service.data;

//Data is stored in a string to optimize speed when converting it for use in distance searches.
public class ZipCodeAndCityData {

  public static final String data;

  static {
    StringBuilder sb = new StringBuilder();
    sb.append("""
            20002,38.9057,76.9861,Washington
                20003,38.882,76.9925,Washington
                20004,38.8921,77.0223,Washington
                20005,38.9042,77.0315,Washington
                20006,38.8962,77.0421,Washington
                20007,38.9147,77.0771,Washington
                20008,38.9372,77.0584,Washington
                20009,38.9197,77.0376,Washington
                20010,38.9322,77.0280,Washington
                20011,38.9513,77.0221,Washington
                20012,38.9771,77.0317,Washington
                20015,38.9677,77.0591,Washington
                20016,38.9381,77.0912,Washington
                20017,38.9377,76.9936,Washington
                20018,38.926,76.9723,Washington
                20019,38.8914,76.9418,Washington
                20020,38.8617,76.9737,Washington
                20024,38.878,77.0198,Washington
                20032,38.8336,77.0036,Washington
                20036,38.9065,77.0414,Washington
                20037,38.8969,77.0503,Washington
                20045,38.8949,77.0279,Washington
                20260,38.9181,77.0222,Washington
                20374,38.8306,77.0162,Washington
                22046,38.88688278,77.18019867,Falls Church
                22201,38.8858,77.0951,Arlington
                22202,38.8565,77.0512,Arlington
                22203,38.8736,77.1178,Arlington
                22204,38.8653,77.09,Arlington
                22205,38.8835,77.1395,Arlington
                22206,38.8431,77.0896,Arlington
                22207,38.9069,77.1236,Arlington
                22209,38.8944,77.0746,Arlington
                22210,38.8804,77.1119,Arlington
                22211,38.8825,77.0795,Fort Myer
                22213,38.8958,77.1624,Arlington
                22214,38.8787,77.1155,Arlington
                22215,38.8795,77.1134,Arlington
                22216,38.8774,77.1155,Arlington
                22219,38.8753,77.1087,Arlington
                20120,38.853,77.4732,Sully Station
                20120,38.853,77.4732,Centreville
                20121,38.8175,77.4601,Centreville
                20122,38.8397,77.4278,Centreville
                20124,38.7803,77.3913,Clifton
                20151,38.8959,77.4456,Chantilly
                20153,38.8188,77.292,Chantilly
                20170,38.9807,77.3799,Herndon
                20171,38.9257,77.398,Oak Hill
                20171,39.9257,77.398,Herndon
                20172,38.9209,77.3953,Herndon
                20190,38.9597,77.3374,Reston
                20190,39.9597,77.3374,Herndon
                20191,38.9325,77.3491,Reston
                20191,39.9325,77.3491,Herndon
                20194,38.9801,77.3417,Reston
                20194,39.9801,77.3417,Herndon
                20195,38.9768,77.3485,Reston
                20195,39.9768,77.3485,Herndon
                22003,38.8382,77.2128,Annandale
                22009,38.7935,77.2718,Springfield
                22009,38.7935,77.2718,Burke
                22015,38.7887,77.2813,Springfield
                22015,38.7887,77.2813,Burke
                22027,38.8947,77.2209,Dunn Loring
                22027,39.8947,77.2209,Vienna
                22031,38.8598,77.2589,Fairfax
                22032,38.8208,77.2909,Fairfax
                22033,38.8779,77.3835,Fairfax
                22034,38.8021,77.4302,Fairfax
                22035,38.8444,77.3086,Fairfax
                22036,38.8021,77.4302,Fairfax
                22039,38.7497,77.3141,Fairfax Station
                22041,38.8465,77.1465,Baileys Crossroads
                22041,39.8465,77.1465,Falls Church
                22042,38.8645,77.1936,Mosby
                22042,39.8645,77.1936,Falls Church
                22043,38.9012,77.1971,Pimmit
                22043,39.9012,77.1971,Falls Church
                22044,38.8591,77.1567,Seven Corners
                22044,38.8591,77.1567,Falls Church
                22066,39.0137,77.3069,Great Falls
                22067,38.9712,77.2305,Greenway
                22067,38.9712,77.2305,Mclean
                22079,38.6817,77.2042,Mason Neck
                22079,38.6817,77.2042,Lorton
                22081,38.8742,77.2303,Merrifield
                22101,38.9398,77.1677,Mclean
                22102,38.9512,77.2307,West Mclean
                22103,38.9335,77.1791,Mclean
                22103,38.9335,77.1791,West Mclean
                22106,38.9375,77.1787,Mclean
                22116,38.8606,77.2595,Mclean
                22121,38.7078,77.0865,Merrifield
                22122,38.7386,77.1853,Mount Vernon
                22124,38.8902,77.3289,Newington
                22124,38.8902,77.3289,Oakton
                22150,38.7731,77.1851,Vienna
                22151,38.8058,77.2071,Springfield
                22151,38.8058,77.2071,North Springfield
                22152,38.7756,77.232,Springfield
                22152,38.7756,77.232,West Springfield
                22153,38.7452,77.2324,Springfield
                22180,38.8958,77.2542,Vienna
                22181,38.9062,77.2949,Vienna
                22182,38.9354,77.2721,Vienna
                22183,38.8985,77.2535,Vienna
                22199,38.7045,77.2282,Lorton
                22303,38.7966,77.0812,Jefferson Manor
                22303,38.7966,77.0812,Alexandria
                22306,38.7569,77.091,Jefferson Manor
                22306,38.7569,77.091,Community
                22307,38.7705,77.0601,Belleview
                22308,38.7296,77.0581,Alexandria
                22309,38.7232,77.1268,Alexandria
                22309,38.7232,77.1268,Engleside
                22310,38.7842,77.1227,Alexandria
                22310,38.7842,77.1227,Franconia
                22312,38.8177,77.1549,Alexandria
                22315,38.755,77.1496,Alexandria
                22315,38.755,77.1496,Kingstowne
                20109,38.755,77.1496,Sudley Springs
                20109,38.755,78.1496,Manassas
                20111,38.7486,77.432,Manassas
                20111,39.7486,78.432,Manassas Park
                20112,38.6662,77.423,Manassas
                20136,38.738,77.5569,Bristow
                20143,38.8519,77.5667,Catharpin
                20155,38.8146,77.6276,Gainesville
                20156,38.8283,77.6248,Gainesville
                20168,39.1399,77.6597,Haymarket
                20169,38.8704,77.6445,Haymarket
                20181,38.6878,77.5671,Nokesville
                20182,38.6872,77.5498,Nokesville
                22026,38.5728,77.3038,Dumfries
                22125,38.6807,77.2603,Occoquan
                22134,38.58,77.4441,Quantico
                22172,38.5576,77.3506,Triangle
                22191,38.6245,77.2692,Woodbridge
                22192,38.6839,77.3246,Woodbridge
                22193,38.6486,77.3446,Dale City
                22193,38.6486,77.3446,Prince William
                22193,38.6486,77.3446,Woodbridge
                22194,38.6356,77.262,Woodbridge
                22195,38.658,77.25,Woodbridge
                20146,38.0438,77.4879,Ashburn
                20147,38.0333,77.4915,Ashburn
                20134,38.1337,77.7065,Purcellville
                20132,38.172,77.7306,Purcellville
                20160,38.1153,77.6916,Purcellville
                20163,38.006,77.429,Sterling
                20164,38.0218,77.4009,Sterling
                20166,38.9643,77.4677,Sterling
                20167,39.0062,77.4288,Sterling
                20166,38.9643,77.4677,Dulles
                20722,38.9321,77.9476,Brentwood
                20715,38.9862,76.7389,Bowie
                20716,38.927,76.7149,Bowie
                20717,38.0066,76.7794,Bowie
                20718,38.9827,76.7574,Bowie
                20719,38.9594,76.7377,Bowie
                20720,38.9768,76.7831,Bowie
                20721,38.9225,76.7879,Bowie
                20781,38.9534,76.9444,Hyattsville
                20782,38.9677,76.9584,Hyattsville
                20783,38.0003,76.9684,Hyattsville
                20784,38.9535,76.8858,Hyattsville
                20785,38.9183,76.8721,Hyattsville
                20787,38.0031,76.9724,Hyattsville
                20788,38.9724,76.9724,Hyattsville
                20768,38.001,76.8768,Greenbelt
                20770,38.0035,76.8755,Greenbelt
                20740,38.9974,76.9284,College Park
                20741,38.996,76.9284,College Park
                20847,39.0838,77.1529,Rockville
                20848,39.0753,77.1165,Rockville
                20849,39.084,77.1537,Rockville
                20850,39.0897,77.1798,Rockville
                20851,39.0789,77.1227,Rockville
                20852,39.0519,77.1242,Rockville
                20853,39.1019,77.0952,Rockville
                20854,39.0353,77.215,Rockville
                20855,39.1396,77.1362,Rockville
                20859,39.0183,77.2089,Rockville
                20901,39.0245,77.0094,Silver Spring
                20902,39.0436,77.0424,Silver Spring
                20903,39.0169,77.9848,Silver Spring
                20904,39.066,77.9783,Silver Spring
                20905,39.066,77.9783,Silver Spring
                20906,39.0912,77.0554,Silver Spring
                20907,39.9965,77.0341,Silver Spring
                20908,39.1035,77.0763,Silver Spring
                20910,39.0015,77.0357,Silver Spring
                20911,39.9954,77.0319,Silver Spring
                20912,39.9819,77.0006,Silver Spring
                20913,39.9829,77.0209,Silver Spring
                20914,39.0756,77.0022,Silver Spring
                20915,39.0431,77.0479,Silver Spring
                20916,39.0796,77.0735,Silver Spring
                20918,39.0213,77.0149,Silver Spring
                20877,39.139336,77.182953,Gaithersburg
                20878,39.115209,77.25161,Gaithersburg
                20879,39.1712,77.1842,Gaithersburg
                20882,39.2308,77.1842,Gaithersburg
                20883,39.1433,77.2016,Gaithersburg
                20884,39.141,77.1944,Gaithersburg
                20885,39.1352,77.2265,Gaithersburg
                20886,39.1827,77.2032,Gaithersburg
                20898,39.1432,77.2015,Gaithersburg
                20830,39.1533,77.0674,Olney
                20832,39.1549,77.0747,Olney
                20874,39.1292,77.2953,Germantown
                20875,39.1733,77.2716,Germantown
                20876,39.2074,77.2311,Germantown
                21716,39.3149,77.6135,Brunswick
                21701,39.4393,77.3428,Frederick
                21702,39.478,77.4561,Frederick
                21703,39.3756,77.4693,Frederick
                21704,39.3474,77.3714,Frederick
                21705,39.4138,77.4082,Frederick
                25443,39.4249,77.8196,Jefferson County 
          
             
   
       
         
        """
    );
    data = sb.toString();
  }
}