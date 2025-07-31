package service.data;

public class ZipCodeAndCityData {
  public static final String data = """
      20013,Washington,DC,38.8933,-77.0146
      20244,Washington,DC,38.8933,-77.0146
      20270,Washington,DC,38.8933,-77.0146
      20372,Washington,DC,38.8933,-77.0146
      20440,Washington,DC,38.9139,-77.0453
      20521,Washington,DC,38.8933,-77.0146
      21139,Riderwood,MD,39.4093,-76.6486
      21252,Towson,MD,39.4015,-76.6019
      21904,Port Deposit,MD,39.6151,-76.0633
      21918,Conowingo,MD,39.6778,-76.1572
      21790,Tuscarora,MD,39.2667,-77.5101
      21538,Kitzmiller,MD,39.4169,-79.2222
      21737,Glenelg,MD,39.2546,-77.0198
      20824,Bethesda,MD,38.9807,-77.1003
      20861,Ashton,MD,39.151,-76.9924
      20903,Silver Spring,MD,39.0095,-76.9846
      20997,Silver Spring,MD,38.9907,-77.0261
      20735,Clinton,MD,38.7549,-76.9026
      20747,District Heights,MD,38.8539,-76.8891
      21619,Chester,MD,38.9583,-76.2842
      20620,Callaway,MD,38.2275,-76.521
      21713,Boonsboro,MD,39.552,-77.6957
      21861,Sharptown,MD,38.5389,-75.7192
      21540,Luke,MD,39.4774,-79.0594
      21562,Westernport,MD,39.4905,-79.014
      24120,Meadows Of Dan,VA,36.725,-80.4022
      20143,Catharpin,VA,38.8543,-77.5719
      24579,Natural Bridge Station,VA,37.598,-79.525
      23897,Yale,VA,36.8373,-77.287
      24236,Damascus,VA,36.6403,-81.7776
      22304,Alexandria,VA,38.8149,-77.121
      22910,Charlottesville,VA,38.0401,-78.4851
      24506,Lynchburg,VA,37.3817,-79.161
      23628,Newport News,VA,37.1959,-76.5248
      23218,Richmond,VA,37.5242,-77.4932
      24001,Roanoke,VA,37.2742,-79.9579
      23453,Virginia Beach,VA,36.776,-76.0766
      23459,Virginia Beach,VA,36.9216,-76.0171
      23471,Virginia Beach,VA,36.7957,-76.0126
      82058,Garrett,WY,42.2035,-105.678
      82717,Gillette,WY,44.3047,-105.4959
      82721,Moorcroft,WY,44.4154,-104.8389
      82729,Sundance,WY,44.4058,-104.3837
      82240,Torrington,WY,42.0624,-104.1917
      82443,Thermopolis,WY,43.7758,-108.3839
      82242,Van Tassell,WY,42.7033,-104.1408
      82836,Dayton,WY,44.878,-107.3026
      82730,Upton,WY,44.0893,-104.6352
      22940,Free Union,VA,38.1944,-78.5849
      23843,Dolphin,VA,36.8318,-77.7887
      23868,Lawrenceville,VA,36.7342,-77.8136
      24513,Lynchburg,VA,37.2458,-79.1335
      20153,Chantilly,VA,38.8318,-77.2888
      24380,Willis,VA,36.8749,-80.4909
      24128,Newport,VA,37.3069,-80.5099
      23001,Achilles,VA,37.2882,-76.426
      23018,Bena,VA,37.271,-76.4555
      22935,Dyke,VA,38.2705,-78.5578
      23162,Studley,VA,37.6757,-77.2908
      23081,Jamestown,VA,37.2235,-76.7833
      22485,King George,VA,38.2811,-77.126
      23024,Bumpass,VA,37.8984,-77.7966
      22989,Woodberry Forest,VA,38.2912,-78.1225
      20030,Washington,DC,38.8933,-77.0146
      20041,Washington,DC,38.8933,-77.0146
      20065,Washington,DC,38.8834,-77.0282
      20206,Washington,DC,38.8933,-77.0146
      20215,Washington,DC,38.8933,-77.0146
      20330,Washington,DC,38.8933,-77.0146
      20398,Washington Navy Yard,DC,38.8951,-77.0364
      20402,Washington,DC,38.8933,-77.0146
      20412,Washington,DC,38.8953,-77.0221
      20426,Washington,DC,38.8933,-77.0146
      24185,Woolwine,VA,36.7921,-80.2774
      23901,Farmville,VA,37.3027,-78.4076
      22135,Quantico,VA,38.5223,-77.2936
      24578,Natural Bridge,VA,37.6606,-79.533
      22840,Mc Gaheysville,VA,38.3712,-78.7411
      22657,Strasburg,VA,38.9884,-78.3386
      23884,Sussex,VA,36.9151,-77.2791
      23694,Lackey,VA,37.2232,-76.559
      22903,Charlottesville,VA,38.0339,-78.4924
      23529,Norfolk,VA,36.9312,-76.2397
      23804,Petersburg,VA,37.2048,-77.3928
      23435,Suffolk,VA,36.8544,-76.4664
      82055,Centennial,WY,41.3391,-105.9945
      82412,Byron,WY,44.7844,-108.544
      82941,Pinedale,WY,42.8543,-109.8561
      23306,Belle Haven,VA,37.5645,-75.8694
      23345,Davis Wharf,VA,37.5529,-75.878
      23416,Oak Hall,VA,37.9229,-75.5386
      23002,Amelia Court House,VA,37.3428,-77.9841
      24533,Clifford,VA,37.538,-78.9463
      24101,Hardy,VA,37.2145,-79.8127
      24352,Laurel Fork,VA,36.7073,-80.5148
      23947,Keysville,VA,37.0411,-78.4699
      23120,Moseley,VA,37.426,-77.7585
      23237,Richmond,VA,37.4011,-77.4615
      23027,Cartersville,VA,37.6479,-78.1389
      20192,Herndon,VA,38.8318,-77.2888
      22734,Remington,VA,38.5309,-77.8037
      22739,Somerville,VA,38.5059,-77.5956
      24105,Indian Valley,VA,36.899,-80.5757
      23065,Gum Spring,VA,37.7982,-77.9375
      24378,Troutdale,VA,36.8611,-81.4929
      24592,South Boston,VA,36.6963,-78.9188
      23005,Ashland,VA,37.7395,-77.4784
      23227,Richmond,VA,37.6247,-77.4351
      24433,Doe Hill,VA,38.4321,-79.4445
      24468,Mustoe,VA,38.386,-79.5592
      22481,Jersey,VA,38.2115,-77.1394
      22517,Mollusk,VA,37.7301,-76.538
      22576,Weems,VA,37.6847,-76.4313
      20177,Leesburg,VA,39.1157,-77.5636
      20197,Waterford,VA,39.1882,-77.63
      22709,Aroda,VA,38.3256,-78.2366
      21114,Crofton,MD,39.0112,-76.6802
      21919,Earleville,MD,39.4271,-75.9403
      21920,Elk Mills,MD,39.658,-75.8282
      20646,La Plata,MD,38.5257,-76.9865
      21648,Madison,MD,38.4782,-76.2412
      21703,Frederick,MD,39.3647,-77.4636
      21777,Point Of Rocks,MD,39.2791,-77.5328
      21154,Street,MD,39.6574,-76.3713
      21044,Columbia,MD,39.2141,-76.8788
      20875,Germantown,MD,39.144,-77.2076
      20894,Bethesda,MD,39.144,-77.2076
      20697,Southern Md Facility,MD,38.8336,-76.8777
      20687,Scotland,MD,38.0828,-76.3477
      21837,Mardela Springs,MD,38.4864,-75.7414
      21203,Baltimore,MD,39.2847,-76.6205
      21212,Baltimore,MD,39.3626,-76.61
      21501,Cumberland,MD,39.6529,-78.7625
      21012,Arnold,MD,39.0476,-76.4941
      23141,Quinton,VA,37.5185,-77.1486
      23443,Townsend,VA,37.1882,-75.969
      23922,Burkeville,VA,37.1952,-78.1961
      22960,Orange,VA,38.2192,-78.0461
      23960,Prospect,VA,37.3058,-78.5462
      24129,New River,VA,37.0964,-80.6081
      22570,Village,VA,37.9472,-76.6154
      24018,Roanoke,VA,37.2503,-80.0525
      24059,Bent Mountain,VA,37.1549,-80.1215
      22821,Dayton,VA,38.4707,-79.085
      23881,Spring Grove,VA,37.1901,-76.9923
      24635,Pocahontas,VA,37.3054,-81.3425
      22630,Front Royal,VA,38.9268,-78.1796
      22311,Alexandria,VA,38.832,-77.12
      24505,Lynchburg,VA,37.4138,-79.1422
      23509,Norfolk,VA,36.8787,-76.2604
      23222,Richmond,VA,37.5748,-77.4267
      24029,Roanoke,VA,37.2742,-79.9579
      24043,Roanoke,VA,37.2692,-79.9399
      82073,Laramie,WY,41.3071,-105.6247
      82648,Powder River,WY,43.0322,-106.9873
      82225,Lusk,WY,42.766,-104.4651
      82414,Cody,WY,44.5231,-109.0756
      23359,Hallwood,VA,37.8837,-75.6041
      22901,Charlottesville,VA,38.0936,-78.5611
      23939,Evergreen,VA,37.3185,-78.7658
      22227,Arlington,VA,38.8808,-77.1129
      24411,Augusta Springs,VA,38.1025,-79.3195
      24178,Villamont,VA,37.3105,-79.7898
      24066,Buchanan,VA,37.5479,-79.7123
      24090,Fincastle,VA,37.4911,-79.8511
      24438,Glen Wilton,VA,37.7529,-79.8189
      24658,Wolford,VA,37.288,-82.0274
      24538,Concord,VA,37.337,-78.9804
      24217,Bee,VA,37.1014,-82.1856
      23840,Dewitt,VA,37.0535,-77.6426
      22160,Springfield,VA,38.8318,-77.2888
      24150,Ripplemead,VA,37.3665,-80.6717
      24520,Alton,VA,36.5894,-79.0202
      23231,Henrico,VA,37.4638,-77.398
      24168,Stanleytown,VA,36.7348,-79.9465
      23315,Carrsville,VA,36.7454,-76.8365
      23086,King William,VA,37.7202,-77.0998
      22482,Kilmarnock,VA,37.7162,-76.382
      22503,Lancaster,VA,37.7395,-76.5002
      22528,Nuttsville,VA,37.7935,-76.5508
      20178,Leesburg,VA,39.1157,-77.5636
      22722,Haywood,VA,38.4535,-78.2522
      23919,Bracey,VA,36.5763,-78.1059
      24073,Christiansburg,VA,37.1353,-80.4188
      24111,Mc Coy,VA,37.1742,-80.3957
      20009,Washington,DC,38.9202,-77.0375
      20017,Washington,DC,38.9367,-76.994
      20037,Washington,DC,38.9014,-77.0504
      20050,Washington,DC,38.8933,-77.0146
      20076,Washington,DC,38.8933,-77.0146
      20228,Washington,DC,38.8951,-77.0364
      20245,Washington,DC,38.8951,-77.0364
      20350,Washington,DC,38.8933,-77.0146
      20429,Washington,DC,38.8933,-77.0146
      20436,Washington,DC,38.8959,-77.0211
      20522,Washington,DC,38.8932,-77.049
      20548,Washington,DC,38.8981,-77.0177
      21090,Linthicum Heights,MD,39.2092,-76.6681
      20688,Solomons,MD,38.3293,-76.4651
      21709,Frederick,MD,39.47,-77.3921
      21531,Friendsville,MD,39.6665,-79.4219
      20759,Fulton,MD,39.1502,-76.93
      20777,Highland,MD,39.1843,-76.9686
      21738,Glenwood,MD,39.2795,-77.0148
      21635,Galena,MD,39.3374,-75.8717
      20838,Barnesville,MD,39.2233,-77.3764
      20848,Rockville,MD,39.144,-77.2076
      20850,Rockville,MD,39.087,-77.168
      20916,Silver Spring,MD,38.9907,-77.0261
      20607,Accokeek,MD,38.672,-77.0162
      20762,Andrews Air Force Base,MD,38.8062,-76.8756
      20622,Charlotte Hall,MD,38.475,-76.8038
      21750,Hancock,MD,39.6991,-78.1762
      21530,Flintstone,MD,39.6993,-78.5739
      20059,Washington,DC,38.8933,-77.0146
      20202,Washington,DC,38.8933,-77.0146
      20289,Washington,DC,38.8933,-77.0146
      20355,Washington,DC,38.8951,-77.0369
      20373,Naval Anacost Annex,DC,38.8951,-77.0364
      20414,Washington,DC,38.884,-77.0221
      20424,Washington,DC,38.8933,-77.0146
      20439,Washington,DC,38.8933,-77.0146
      20447,Washington,DC,38.8847,-77.0252
      20591,Washington,DC,38.8933,-77.0146
      20593,Washington,DC,38.8933,-77.0146
      21140,Riva,MD,38.9504,-76.5854
      21228,Catonsville,MD,39.2782,-76.7401
      21911,Rising Sun,MD,39.6882,-76.0492
      21631,East New Market,MD,38.5921,-75.9568
      21704,Frederick,MD,39.3455,-77.3832
      21727,Emmitsburg,MD,39.694,-77.3357
      21769,Middletown,MD,39.4416,-77.5502
      21084,Jarrettsville,MD,39.6162,-76.4684
      21085,Joppa,MD,39.4242,-76.3541
      20703,Lanham,MD,38.8336,-76.8777
      20704,Beltsville,MD,39.0348,-76.9075
      20712,Mount Rainier,MD,38.9431,-76.9652
      20749,Fort Washington,MD,38.8336,-76.8777
      20783,Hyattsville,MD,39.0005,-76.9723
      20690,Tall Timbers,MD,38.1653,-76.5399
      23405,Machipongo,VA,37.4014,-75.9082
      24161,Sandy Level,VA,36.991,-79.5614
      23942,Green Bay,VA,37.1234,-78.3072
      20106,Amissville,VA,38.6842,-78.0168
      22623,Chester Gap,VA,38.8537,-78.1412
      22716,Castleton,VA,38.6032,-78.1208
      22848,Pleasant Valley,VA,38.3847,-78.8914
      23828,Branchville,VA,36.5787,-77.2704
      22412,Fredericksburg,VA,38.1847,-77.6626
      24605,Bluefield,VA,37.2526,-81.2712
      24230,Coeburn,VA,36.9605,-82.4735
      22030,Fairfax,VA,38.8458,-77.3242
      24503,Lynchburg,VA,37.4376,-79.205
      24113,Martinsville,VA,36.6796,-79.8652
      23602,Newport News,VA,37.1132,-76.5179
      23506,Norfolk,VA,36.9312,-76.2397
      23282,Richmond,VA,37.5538,-77.4603
      23295,Richmond,VA,37.5532,-77.4621
      24024,Roanoke,VA,37.2742,-79.9579
      23458,Virginia Beach,VA,36.8529,-75.978
      23464,Virginia Beach,VA,36.7978,-76.1759
      82514,Fort Washakie,WY,43.0048,-108.8964
      83123,La Barge,WY,42.2473,-110.2109
      83128,Alpine,WY,43.1635,-111.018
      82929,Little America,WY,41.5436,-109.859
      82944,Robertson,WY,41.0993,-110.5007
      23337,Wallops Island,VA,37.9186,-75.4905
      23417,Onancock,VA,37.7102,-75.7528
      23488,Withams,VA,37.9482,-75.6019
      22201,Arlington,VA,38.8871,-77.0932
      22207,Arlington,VA,38.9033,-77.1263
      22244,Arlington,VA,38.8545,-77.052
      24486,Weyers Cave,VA,38.2931,-78.9235
      24487,Williamsville,VA,38.1898,-79.5879
      23114,Midlothian,VA,37.4815,-77.6419
      23297,Richmond,VA,37.3897,-77.5613
      24131,Paint Bank,VA,37.5745,-80.2544
      22735,Reva,VA,38.4604,-78.1572
      20195,Reston,VA,38.8318,-77.2888
      22032,Fairfax,VA,38.8177,-77.2925
      22066,Great Falls,VA,39.0039,-77.3083
      22082,Merrifield,VA,38.8743,-77.2269
      22095,Herndon,VA,38.9696,-77.3861
      22096,Reston,VA,38.8318,-77.2888
      23038,Columbia,VA,37.7147,-78.1794
      23230,Richmond,VA,37.5921,-77.4952
      23430,Smithfield,VA,36.9811,-76.6368
      20102,Dulles,VA,39.0853,-77.6452
      20118,Middleburg,VA,38.9687,-77.7355
      20163,Sterling,VA,39.0062,-77.4286
      23974,Victoria,VA,36.9835,-78.2372
      22727,Madison,VA,38.37,-78.2976
      22967,Roseland,VA,37.8077,-78.9712
      21542,Midland,MD,39.5901,-78.9497
      20779,Tracys Landing,MD,38.7671,-76.5752
      20038,Washington,DC,38.8933,-77.0146
      20091,Washington,DC,38.8933,-77.0146
      20207,Washington,DC,38.8933,-77.0146
      20230,Washington,DC,38.8951,-77.0364
      20277,Washington,DC,38.8933,-77.0146
      20413,Washington,DC,38.8933,-77.0146
      20423,Washington,DC,38.8933,-77.0146
      20434,Washington,DC,38.8933,-77.0146
      20437,Washington,DC,38.9028,-77.0485
      20441,Washington,DC,38.9239,-77.0363
      20501,Washington,DC,38.8987,-77.0362
      20524,Washington,DC,38.9024,-77.0326
      20529,Washington,DC,38.8973,-77.0142
      21207,Gwynn Oak,MD,39.3296,-76.7341
      21208,Pikesville,MD,39.3764,-76.729
      21930,Georgetown,MD,39.3662,-75.8845
      20643,Ironsides,MD,38.5039,-77.1483
      21664,Secretary,MD,38.6093,-75.9474
      20877,Gaithersburg,MD,39.1419,-77.189
      20745,Oxon Hill,MD,38.8108,-76.9898
      20791,Capitol Heights,MD,38.8336,-76.8777
      20609,Avenue,MD,38.2826,-76.7466
      20656,Loveville,MD,38.3593,-76.6833
      21782,Sharpsburg,MD,39.4424,-77.7511
      22945,Ivy,VA,38.0654,-78.5958
      22203,Arlington,VA,38.8738,-77.1142
      24440,Greenville,VA,38.0018,-79.1359
      24467,Mount Sidney,VA,38.2612,-78.973
      23921,Buckingham,VA,37.5833,-78.5985
      23937,Drakes Branch,VA,36.9688,-78.5615
      23234,Richmond,VA,37.4373,-77.4788
      23830,Carson,VA,37.0393,-77.4351
      22027,Dunn Loring,VA,38.8954,-77.2214
      22107,Mc Lean,VA,38.9321,-77.1841
      22156,Springfield,VA,38.8318,-77.2888
      22312,Alexandria,VA,38.8191,-77.1484
      24176,Union Hall,VA,37.0131,-79.6864
      23146,Rockville,VA,37.7337,-77.7
      23242,Henrico,VA,37.4638,-77.398
      20164,Sterling,VA,39.023,-77.3994
      20598,Dhs,VA,38.9904,-77.4477
      22964,Piney River,VA,37.7051,-79.0231
      24177,Vesta,VA,36.7241,-80.3581
      22025,Dumfries,VA,38.5993,-77.3403
      24324,Draper,VA,36.9697,-80.8188
      22746,Viewtown,VA,38.6457,-78.0258
      22812,Bridgewater,VA,38.3859,-78.9937
      22841,Mount Crawford,VA,38.3457,-78.8957
      23844,Drewryville,VA,36.6854,-77.3591
      23690,Yorktown,VA,37.2287,-76.5423
      22350,Alexandria,VA,38.8045,-77.0509
      24203,Bristol,VA,36.5965,-82.1885
      22031,Fairfax,VA,38.8604,-77.2649
      20110,Manassas,VA,38.7492,-77.4878
      23701,Portsmouth,VA,36.8089,-76.3671
      23704,Portsmouth,VA,36.8298,-76.3146
      23274,Richmond,VA,37.5242,-77.4932
      24022,Roanoke,VA,37.2784,-79.9332
      24402,Staunton,VA,38.1593,-79.0629
      23437,Suffolk,VA,36.6526,-76.792
      83120,Freedom,WY,43.0172,-111.0292
      83126,Smoot,WY,42.6192,-110.9224
      82215,Hartville,WY,42.3334,-104.7296
      82832,Banner,WY,44.6003,-106.7954
      82322,Bairoil,WY,42.2327,-107.5633
      82935,Green River,WY,41.5196,-109.4714
      83011,Kelly,WY,43.6094,-110.5442
      82723,Osage,WY,43.999,-104.4226
      21060,Glen Burnie,MD,39.1702,-76.5798
      21031,Hunt Valley,MD,39.4805,-76.6553
      21057,Glen Arm,MD,39.4575,-76.5153
      21094,Lutherville Timonium,MD,39.439,-76.5921
      21105,Maryland Line,MD,39.7114,-76.6595
      21117,Owings Mills,MD,39.4269,-76.7769
      21227,Halethorpe,MD,39.2309,-76.6969
      21636,Goldsboro,MD,39.023,-75.7926
      21640,Henderson,MD,39.0672,-75.7948
      21797,Woodbine,MD,39.3464,-77.0647
      21762,Libertytown,MD,39.4822,-77.2468
      21050,Forest Hill,MD,39.5755,-76.4008
      21132,Pylesville,MD,39.6959,-76.4113
      20811,Bethesda,MD,38.9806,-77.1008
      20709,Laurel,MD,39.0993,-76.8483
      20710,Bladensburg,MD,38.9421,-76.9261
      20913,Takoma Park,MD,38.9779,-77.0075
      21721,Chewsville,MD,39.6425,-77.6372
      21201,Baltimore,MD,39.2946,-76.6252
      21290,Baltimore,MD,39.2933,-76.6238
      20002,Washington,DC,38.9024,-76.9901
      20208,Washington,DC,38.8966,-77.0117
      20229,Washington,DC,38.8933,-77.0146
      20370,Washington,DC,38.8933,-77.0146
      20460,Washington,DC,38.8764,-77.0188
      20468,Washington,DC,38.8933,-77.0146
      20470,Washington,DC,38.8933,-77.0146
      20528,Washington,DC,38.8951,-77.0369
      20543,Washington,DC,38.8933,-77.0146
      20577,Washington,DC,38.9008,-77.0345
      23875,Prince George,VA,37.2333,-77.2747
      24084,Dublin,VA,37.1057,-80.6853
      24483,Vesuvius,VA,37.8378,-79.2135
      22853,Timberville,VA,38.6476,-78.7717
      23837,Courtland,VA,36.7225,-77.0783
      23878,Sedley,VA,36.776,-76.9841
      22534,Partlow,VA,38.0641,-77.6586
      22406,Fredericksburg,VA,38.3796,-77.5349
      24283,Saint Paul,VA,36.9323,-82.3418
      23693,Yorktown,VA,37.1256,-76.4469
      23321,Chesapeake,VA,36.8012,-76.423
      24541,Danville,VA,36.5779,-79.4411
      23515,Norfolk,VA,36.9312,-76.2397
      23518,Norfolk,VA,36.9202,-76.216
      23284,Richmond,VA,37.5494,-77.4512
      24012,Roanoke,VA,37.3029,-79.9322
      23436,Suffolk,VA,36.8926,-76.5142
      23439,Suffolk,VA,36.7461,-76.6653
      23455,Virginia Beach,VA,36.8881,-76.1446
      82083,Rock River,WY,41.7461,-105.9746
      82422,Emblem,WY,44.5058,-108.3918
      82432,Manderson,WY,44.2694,-107.964
      82217,Hawk Springs,WY,41.7861,-104.2647
      82640,Linch,WY,43.5651,-106.1728
      82336,Wamsutter,WY,41.6583,-108.1517
      83002,Jackson,WY,43.4799,-110.7624
      23407,Mappsville,VA,37.846,-75.5666
      23442,Temperanceville,VA,37.8954,-75.5528
      22936,Earlysville,VA,38.1578,-78.4919
      22947,Keswick,VA,38.0531,-78.3396
      24469,New Hope,VA,38.1979,-78.9059
      23893,White Plains,VA,36.6335,-77.9592
      23920,Brodnax,VA,36.7319,-77.9898
      24624,Keen Mountain,VA,37.2023,-81.9862
      24627,Mavisdale,VA,37.288,-82.0274
      24657,Whitewood,VA,37.2251,-81.8605
      24220,Birchleaf,VA,37.1394,-82.2433
      20191,Reston,VA,38.9318,-77.3527
      20194,Reston,VA,38.9807,-77.3419
      20196,Reston,VA,38.8318,-77.2888
      22158,Springfield,VA,38.8318,-77.2888
      22963,Palmyra,VA,37.8931,-78.3386
      23879,Skippers,VA,36.5958,-77.5921
      23058,Glen Allen,VA,37.5313,-77.4161
      24148,Ridgeway,VA,36.5874,-79.8686
      23168,Toano,VA,37.3901,-76.8253
      23177,Walkerton,VA,37.7554,-77.0185
      20189,Dulles,VA,39.009,-77.4421
      22715,Brightwood,VA,38.4114,-78.1698
      23068,Hallieford,VA,37.4937,-76.3402
      20053,Washington,DC,38.8933,-77.0146
      20319,Washington,DC,38.8667,-77.0166
      20394,Washington,DC,38.8933,-77.0146
      20408,Washington,DC,38.8933,-77.0146
      20417,Washington,DC,38.907,-77.0058
      20422,Washington,DC,38.8933,-77.0146
      20553,Washington,DC,38.8873,-77.0231
      20571,Washington,DC,38.9006,-77.0346
      20575,Washington,DC,38.8933,-77.0146
      20579,Washington,DC,38.9043,-77.0446
      20586,Washington,DC,38.9022,-77.0474
      20597,Washington,DC,38.8933,-77.0146
      56944,Washington,DC,38.8952,-77.0365
      21402,Annapolis,MD,38.9871,-76.4715
      20685,Saint Leonard,MD,38.4501,-76.511
      21639,Greensboro,MD,38.9616,-75.8059
      21784,Sykesville,MD,39.4567,-76.9696
      20617,Bryantown,MD,38.5426,-76.8465
      20677,Port Tobacco,MD,38.4994,-77.0419
      20695,White Plains,MD,38.5974,-76.9903
      21677,Woolford,MD,38.5026,-76.1833
      21759,Ladiesburg,MD,39.5694,-77.2905
      20859,Potomac,MD,39.144,-77.2076
      20880,Washington Grove,MD,39.1388,-77.1726
      20706,Lanham,MD,38.9675,-76.8551
      21658,Queenstown,MD,39.0025,-76.1424
      21624,Claiborne,MD,38.8368,-76.2714
      21671,Tilghman,MD,38.7063,-76.3377
      21673,Trappe,MD,38.6647,-76.0507
      21814,Bivalve,MD,38.2953,-75.8914
      21850,Pittsville,MD,38.3755,-75.4076
      21822,Eden,MD,38.2807,-75.651
      21206,Baltimore,MD,39.3365,-76.5411
      21278,Baltimore,MD,39.2847,-76.6205
      21279,Baltimore,MD,39.2847,-76.6205
      22511,Lottsburg,VA,37.9792,-76.5018
      24586,Ringgold,VA,36.6035,-79.2988
      20136,Bristow,VA,38.7343,-77.5474
      20181,Nokesville,VA,38.7,-77.5483
      22747,Washington,VA,38.7078,-78.1566
      24439,Goshen,VA,37.9878,-79.4773
      22815,Broadway,VA,38.6083,-78.7875
      24649,Swords Creek,VA,37.0738,-81.9084
      24244,Duffield,VA,36.7114,-82.8337
      24251,Gate City,VA,36.646,-82.6112
      24290,Weber City,VA,36.6137,-82.5618
      22405,Fredericksburg,VA,38.3365,-77.4366
      24279,Pound,VA,37.0927,-82.6016
      22402,Fredericksburg,VA,38.2996,-77.4897
      23806,Virginia State University,VA,37.2327,-77.385
      23286,Richmond,VA,37.5242,-77.4932
      23292,Richmond,VA,37.5242,-77.4932
      24014,Roanoke,VA,37.2327,-79.9463
      23432,Suffolk,VA,36.8668,-76.5598
      82420,Cowley,WY,44.8841,-108.4638
      82633,Douglas,WY,42.7626,-105.3855
      82712,Beulah,WY,44.5436,-104.0745
      82430,Kirby,WY,43.8042,-108.1805
      82053,Burns,WY,41.2003,-104.3155
      82635,Edgerton,WY,43.4074,-106.2638
      82222,Lance Creek,WY,43.0325,-104.6419
      82423,Frannie,WY,44.9718,-108.6221
      82214,Guernsey,WY,42.2655,-104.7512
      83115,Daniel,WY,42.9176,-110.1336
      82945,Superior,WY,41.7643,-108.9681
      82937,Lyman,WY,41.3291,-110.2926
      82701,Newcastle,WY,43.8511,-104.2262
      23105,Mannboro,VA,37.345,-77.9449
      22240,Arlington,VA,38.8566,-77.0518
      22241,Arlington,VA,38.8808,-77.1129
      24130,Oriskany,VA,37.6165,-79.9837
      24550,Evington,VA,37.261,-79.2317
      24381,Woodlawn,VA,36.7879,-80.8824
      23030,Charles City,VA,37.3663,-77.1082
      23964,Red Oak,VA,36.7724,-78.6321
      22718,Elkwood,VA,38.4652,-77.817
      22737,Rixeyville,VA,38.5852,-78.0282
      22044,Falls Church,VA,38.8589,-77.1548
      22067,Greenway,VA,38.9645,-77.2331
      22159,Springfield,VA,38.8318,-77.2888
      22310,Alexandria,VA,38.7794,-77.1194
      22643,Markham,VA,38.904,-78.0019
      22728,Midland,VA,38.5671,-77.7127
      24067,Callaway,VA,37.0285,-80.0496
      22602,Winchester,VA,39.1501,-78.269
      23155,Severn,VA,37.4182,-76.5084
      23178,Ware Neck,VA,37.4004,-76.4529
      23063,Goochland,VA,37.7688,-78.0112
      23075,Henrico,VA,37.546,-77.3278
      23487,Windsor,VA,36.8369,-76.7324
      24263,Jonesville,VA,36.6896,-83.1362
      20117,Middleburg,VA,38.9687,-77.7355
      20135,Bluemont,VA,39.0823,-77.8467
      20158,Hamilton,VA,39.1383,-77.6573
      20175,Leesburg,VA,39.042,-77.6054
      20176,Leesburg,VA,39.1821,-77.5359
      23938,Dundas,VA,36.9053,-78.01
      23944,Kenbridge,VA,36.933,-78.1242
      23056,Foster,VA,37.4529,-76.3849
      23064,Grimstead,VA,37.4961,-76.3
      23125,New Point,VA,37.3438,-76.2878
      23927,Clarksville,VA,36.624,-78.5569
      24061,Blacksburg,VA,37.1791,-80.3515
      20001,Washington,DC,38.9122,-77.0177
      20011,Washington,DC,38.9518,-77.0203
      20044,Washington,DC,38.8933,-77.0146
      20060,Washington,DC,38.918,-77.0204
      20061,Washington,DC,38.8933,-77.0146
      20219,Washington,DC,38.8933,-77.0146
      20314,Washington,DC,38.8933,-77.0146
      20389,Washington,DC,38.8933,-77.0146
      20404,Washington,DC,38.8992,-77.0089
      20418,Washington,DC,38.9043,-77.0572
      20420,Washington,DC,38.9035,-77.0276
      20531,Washington,DC,38.8938,-77.0218
      20535,Washington,DC,38.8941,-77.0251
      20538,Washington,DC,38.8933,-77.0146
      56933,Washington,DC,38.8952,-77.0365
      56972,Washington,DC,38.8951,-77.0364
      21013,Baldwin,MD,39.5194,-76.4927
      21120,Parkton,MD,39.6422,-76.6737
      21088,Lineboro,MD,39.7187,-76.8439
      21104,Marriottsville,MD,39.3342,-76.9132
      21755,Jefferson,MD,39.3653,-77.5441
      21523,Bloomington,MD,39.4861,-79.083
      21610,Betterton,MD,39.3655,-76.0639
      20860,Sandy Spring,MD,39.1503,-77.0291
      20876,Germantown,MD,39.188,-77.2358
      20908,Silver Spring,MD,38.9907,-77.0261
      20708,Laurel,MD,39.0499,-76.8345
      20720,Bowie,MD,38.9885,-76.791
      20773,Upper Marlboro,MD,38.8159,-76.7497
      20650,Leonardtown,MD,38.2774,-76.638
      21601,Easton,MD,38.7768,-76.0758
      21804,Salisbury,MD,38.3508,-75.5338
      21829,Girdletree,MD,38.0958,-75.3902
      21218,Baltimore,MD,39.3265,-76.6048
      21231,Baltimore,MD,39.2892,-76.59
      21273,Baltimore,MD,39.2847,-76.6205
      23358,Hacksneck,VA,37.6393,-75.865
      22932,Crozet,VA,38.1296,-78.7106
      22202,Arlington,VA,38.8565,-77.0592
      22205,Arlington,VA,38.8836,-77.1395
      22210,Arlington,VA,38.8808,-77.1129
      22211,Fort Myer,VA,38.8921,-77.0794
      22952,Lyndhurst,VA,37.9744,-78.9361
      24482,Verona,VA,38.2037,-79.0059
      24083,Daleville,VA,37.4125,-79.9211
      22580,Woodford,VA,38.0847,-77.4399
      20171,Herndon,VA,38.9252,-77.3928
      22036,Fairfax,VA,38.7351,-77.0797
      22041,Falls Church,VA,38.8502,-77.1448
      22182,Vienna,VA,38.928,-77.2649
      20115,Marshall,VA,38.8405,-77.8911
      20119,Catlett,VA,38.637,-77.6383
      20138,Calverton,VA,38.6338,-77.6869
      24124,Narrows,VA,37.3198,-80.8548
      22968,Ruckersville,VA,38.2586,-78.4074
      24055,Bassett,VA,36.7534,-80.0055
      23314,Carrollton,VA,36.955,-76.543
      23110,Mattaponi,VA,37.6135,-76.8101
      20146,Ashburn,VA,39.0853,-77.6452
      23952,Lunenburg,VA,36.9291,-78.2915
      23917,Boydton,VA,36.6544,-78.3752
      23032,Church View,VA,37.675,-76.6629
      21521,Barton,MD,39.5331,-79.0281
      20724,Laurel,MD,39.0958,-76.8155
      20733,Churchton,MD,38.8018,-76.5248
      23124,New Kent,VA,37.553,-77.0742
      23140,Providence Forge,VA,37.4418,-77.0436
      22923,Barboursville,VA,38.2095,-78.3098
      24557,Gretna,VA,36.9695,-79.3389
      22740,Sperryville,VA,38.6203,-78.2466
      24472,Raphine,VA,37.9374,-79.2219
      24260,Honaker,VA,37.0395,-82.0246
      22641,Strasburg,VA,39.0808,-78.3913
      22824,Edinburg,VA,38.8432,-78.6003
      22845,Orkney Springs,VA,38.7937,-78.8111
      23691,Yorktown,VA,37.2517,-76.552
      22331,Alexandria,VA,38.8013,-77.0707
      23663,Hampton,VA,37.0318,-76.3199
      23667,Hampton,VA,37.0193,-76.3318
      24502,Lynchburg,VA,37.3825,-79.2181
      23601,Newport News,VA,37.058,-76.4607
      23606,Newport News,VA,37.0768,-76.4967
      23608,Newport News,VA,37.1526,-76.542
      23504,Norfolk,VA,36.8586,-76.2686
      23507,Norfolk,VA,36.8645,-76.3004
      23705,Portsmouth,VA,36.8686,-76.3552
      23187,Williamsburg,VA,37.3105,-76.7468
      82229,Shawnee,WY,42.7477,-105.0097
      82510,Arapahoe,WY,42.9679,-108.4941
      82512,Crowheart,WY,43.3716,-109.296
      82003,Cheyenne,WY,41.2191,-104.6612
      82007,Cheyenne,WY,41.1084,-104.8107
      82838,Parkman,WY,44.965,-107.3254
      82902,Rock Springs,WY,41.5875,-109.2029
      23308,Bloxom,VA,37.8273,-75.6156
      23409,Mears,VA,37.8692,-75.6354
      23441,Tasley,VA,37.7104,-75.7005
      22242,Arlington,VA,38.8509,-77.0523
      24639,Raven,VA,37.1481,-81.8896
      22428,Bowling Green,VA,38.0496,-77.3466
      22546,Ruther Glen,VA,37.9451,-77.4714
      22552,Sparta,VA,38.0094,-77.2251
      23967,Saxe,VA,36.9053,-78.6321
      23112,Midlothian,VA,37.431,-77.6545
      23113,Midlothian,VA,37.5109,-77.6429
      22736,Richardsville,VA,38.3924,-77.7195
      22741,Stevensburg,VA,38.4441,-77.8842
      24272,Nora,VA,37.0182,-82.35
      23115,Millers Tavern,VA,37.8282,-76.945
      22035,Fairfax,VA,38.8557,-77.3616
      22118,Merrifield,VA,38.8318,-77.2888
      20140,Rectortown,VA,38.9163,-77.8648
      22639,Hume,VA,38.8273,-77.9949
      22654,Star Tannery,VA,39.0908,-78.4343
      23039,Crozier,VA,37.6718,-77.794
      23228,Henrico,VA,37.4638,-77.398
      24054,Axton,VA,36.6596,-79.712
      23188,Williamsburg,VA,37.3178,-76.7634
      23941,Fort Mitchell,VA,36.9182,-78.4861
      23138,Port Haywood,VA,37.386,-76.3172
      24529,Buffalo Junction,VA,36.6168,-78.6093
      23149,Saluda,VA,37.5949,-76.5921
      20261,Washington,DC,38.8933,-77.0146
      20391,Washington Navy Yard,DC,38.8951,-77.0364
      20392,Washington,DC,38.8933,-77.0146
      20433,Washington,DC,38.9,-77.042
      20505,Washington,DC,38.8933,-77.0146
      20546,Washington,DC,38.891,-77.0211
      20551,Washington,DC,38.892,-77.0452
      20573,Washington,DC,38.8933,-77.0146
      21225,Brooklyn,MD,39.2259,-76.6153
      20639,Huntingtown,MD,38.6095,-76.6003
      21912,Warwick,MD,39.4283,-75.7996
      21916,Childs,MD,39.6462,-75.8716
      20612,Benedict,MD,38.5093,-76.6797
      21622,Church Creek,MD,38.4278,-76.1696
      21541,Mc Henry,MD,39.5656,-79.3823
      21041,Ellicott City,MD,39.2364,-76.9419
      20748,Temple Hills,MD,38.8222,-76.9478
      20771,Greenbelt,MD,39.0046,-76.8755
      21668,Sudlersville,MD,39.1823,-75.85
      21890,Westover,MD,38.0927,-75.8882
      21733,Fairplay,MD,39.5594,-77.7604
      21746,Hagerstown,MD,39.5638,-77.7206
      21813,Bishopville,MD,38.4296,-75.1855
      21223,Baltimore,MD,39.287,-76.6476
      21270,Baltimore,MD,39.2847,-76.6205
      21297,Baltimore,MD,39.2847,-76.6205
      24581,Norwood,VA,37.6432,-78.8089
      22435,Callao,VA,37.9773,-76.5732
      22567,Unionville,VA,38.2383,-77.9193
      23966,Rice,VA,37.2721,-78.2793
      24301,Pulaski,VA,37.0567,-80.771
      24250,Fort Blackmore,VA,36.7438,-82.6102
      22430,Brooke,VA,38.3857,-77.3743
      24361,Meadowview,VA,36.7612,-81.8512
      22581,Zacata,VA,38.1218,-76.7903
      23322,Chesapeake,VA,36.6434,-76.242
      23325,Chesapeake,VA,36.814,-76.2406
      22801,Harrisonburg,VA,38.4489,-78.8714
      24501,Lynchburg,VA,37.353,-79.1557
      23514,Norfolk,VA,36.8468,-76.2852
      23703,Portsmouth,VA,36.8695,-76.3869
      24143,Radford,VA,37.1226,-80.5629
      24011,Roanoke,VA,37.269,-79.942
      24013,Roanoke,VA,37.2677,-79.9247
      24032,Roanoke,VA,37.2742,-79.9579
      83121,Frontier,WY,41.8141,-110.5371
      82450,Wapiti,WY,44.4686,-109.4377
      24574,Monroe,VA,37.5414,-79.1703
      22843,Mount Solon,VA,38.3328,-79.1028
      24620,Hurley,VA,37.4017,-82.0262
      22538,Rappahannock Academy,VA,38.2079,-77.2502
      23850,Ford,VA,37.1349,-77.7364
      22034,Fairfax,VA,38.8318,-77.2888
      22712,Bealeton,VA,38.5453,-77.7561
      22742,Sumerduck,VA,38.4674,-77.7044
      24065,Boones Mill,VA,37.1331,-79.9556
      24136,Pembroke,VA,37.3312,-80.5975
      23131,Ordinary,VA,37.3122,-76.5188
      23059,Glen Allen,VA,37.7284,-77.5544
      24413,Blue Grass,VA,38.5152,-79.5613
      24248,Ewing,VA,36.6237,-83.5047
      22725,Leon,VA,38.4285,-78.2678
      23021,Bohannon,VA,37.3905,-76.3614
      23968,Skipwith,VA,36.7317,-78.5306
      24580,Nelson,VA,36.5586,-78.671
      21556,Pinto,MD,39.5725,-78.844
      21766,Little Orleans,MD,39.6876,-78.3781
      23413,Nassawadox,VA,37.4728,-75.858
      23419,Oyster,VA,37.3074,-75.9269
      24076,Claudville,VA,36.5896,-80.4184
      24069,Cascade,VA,36.5927,-79.6574
      22572,Warsaw,VA,37.9695,-76.7665
      24555,Glasgow,VA,37.643,-79.459
      24266,Lebanon,VA,36.8809,-82.0956
      24375,Sugar Grove,VA,36.7736,-81.4084
      23829,Capron,VA,36.7243,-77.2394
      24219,Big Stone Gap,VA,36.8581,-82.7627
      23324,Chesapeake,VA,36.8056,-76.2666
      20108,Manassas,VA,38.7447,-77.4872
      23517,Norfolk,VA,36.8695,-76.2945
      23224,Richmond,VA,37.4955,-77.471
      23276,Richmond,VA,37.5242,-77.4932
      23285,Richmond,VA,37.5242,-77.4932
      23479,Virginia Beach,VA,36.7957,-76.0126
      22604,Winchester,VA,39.1676,-78.1686
      82051,Bosler,WY,41.5735,-105.6112
      82714,Devils Tower,WY,44.5248,-104.6867
      82520,Lander,WY,42.9208,-108.5913
      82223,Lingle,WY,42.1346,-104.332
      82050,Albin,WY,41.4342,-104.1505
      82081,Meriden,WY,41.5424,-104.2866
      82602,Casper,WY,42.8896,-106.357
      20035,Washington,DC,38.8933,-77.0146
      20045,Washington,DC,38.8966,-77.0319
      20216,Washington,DC,38.8919,-77.0141
      20218,Washington,DC,38.8933,-77.0146
      20232,Washington,DC,38.9006,-77.0391
      20388,Washington Navy Yard,DC,38.8951,-77.0364
      20503,Washington,DC,38.9007,-77.0431
      20557,Washington,DC,38.8874,-77.0047
      20565,Washington,DC,38.8919,-77.0189
      21403,Annapolis,MD,38.9524,-76.491
      21405,Annapolis,MD,39.0305,-76.5515
      21087,Kingsville,MD,39.4558,-76.4147
      21204,Towson,MD,39.4072,-76.6038
      21903,Perryville,MD,39.5649,-76.0592
      21921,Elkton,MD,39.6264,-75.8458
      20693,Welcome,MD,38.4672,-77.095
      21794,West Friendship,MD,39.2934,-76.966
      20837,Poolesville,MD,39.1386,-77.4067
      20882,Gaithersburg,MD,39.2335,-77.1458
      20715,Bowie,MD,38.9797,-76.7435
      20719,Bowie,MD,38.8336,-76.8777
      20775,Upper Marlboro,MD,38.8336,-76.8777
      20784,Hyattsville,MD,38.9513,-76.8958
      20618,Bushwood,MD,38.2844,-76.7929
      20619,California,MD,38.3006,-76.5312
      20670,Patuxent River,MD,38.2791,-76.4381
      20692,Valley Lee,MD,38.1899,-76.5087
      21838,Marion Station,MD,38.0266,-75.7579
      21275,Baltimore,MD,39.2847,-76.6205
      23301,Accomac,VA,37.7159,-75.6803
      23414,Nelsonia,VA,37.8169,-75.5832
      24595,Sweet Briar,VA,37.5529,-79.0664
      24593,Spout Spring,VA,37.3643,-78.9059
      22209,Arlington,VA,38.8926,-77.0753
      23123,New Canton,VA,37.6645,-78.3112
      20170,Herndon,VA,38.9839,-77.3675
      22106,Mc Lean,VA,38.9335,-77.1797
      22306,Alexandria,VA,38.7589,-77.0873
      23072,Hayes,VA,37.2916,-76.4905
      23047,Doswell,VA,37.8417,-77.5125
      23233,Henrico,VA,37.4638,-77.398
      24078,Collinsville,VA,36.7238,-79.9142
      22547,Sealston,VA,38.2711,-77.1726
      24243,Dryden,VA,36.7818,-82.9305
      22743,Syria,VA,38.4972,-78.3229
      23076,Hudgins,VA,37.4766,-76.3089
      23915,Baskerville,VA,36.7236,-78.279
      23071,Hartfield,VA,37.5594,-76.4769
      23092,Locust Hill,VA,37.5826,-76.4984
      21524,Corriganville,MD,39.6967,-78.8031
      21539,Lonaconing,MD,39.5757,-78.9915
      20201,Washington,DC,38.8933,-77.0146
      20227,Washington,DC,38.8933,-77.0146
      20237,Washington,DC,38.895,-77.0367
      20239,Washington,DC,38.8933,-77.0146
      20407,Washington,DC,38.8933,-77.0146
      20421,Washington,DC,38.8933,-77.0146
      20431,Washington,DC,38.8986,-77.0428
      20511,Washington,DC,38.8951,-77.0364
      21412,Annapolis,MD,38.9742,-76.5949
      21111,Monkton,MD,39.5662,-76.5979
      21222,Dundalk,MD,39.2655,-76.4935
      21236,Nottingham,MD,39.3914,-76.4871
      20678,Prince Frederick,MD,38.5336,-76.5955
      21660,Ridgely,MD,38.9568,-75.8848
      20603,Waldorf,MD,38.6294,-76.9769
      20675,Pomfret,MD,38.5855,-77.0093
      21701,Frederick,MD,39.4461,-77.335
      21754,Ijamsville,MD,39.3267,-77.2964
      21078,Havre De Grace,MD,39.5523,-76.1171
      20723,Laurel,MD,39.1208,-76.8435
      20797,Southern Md Facility,MD,38.8336,-76.8777
      20635,Helen,MD,38.3121,-76.6077
      21612,Bozman,MD,38.7515,-76.2764
      21719,Cascade,MD,39.6958,-77.4955
      21740,Hagerstown,MD,39.632,-77.7372
      21747,Hagerstown,MD,39.6418,-77.72
      21781,Saint James,MD,39.5699,-77.7607
      21209,Baltimore,MD,39.3716,-76.6744
      21211,Baltimore,MD,39.3316,-76.6336
      21298,Baltimore,MD,39.2847,-76.6205
      24553,Gladstone,VA,37.5473,-78.8508
      22579,Wicomico Church,VA,37.8025,-76.3636
      23824,Blackstone,VA,37.0916,-77.9851
      24082,Critz,VA,36.6211,-80.1298
      24565,Java,VA,36.8586,-79.187
      23909,Farmville,VA,37.3016,-78.3949
      24087,Elliston,VA,37.2165,-80.2325
      22846,Penn Laird,VA,38.375,-78.7795
      22842,Mount Jackson,VA,38.7857,-78.6803
      24316,Broadford,VA,36.9546,-81.673
      24612,Doran,VA,37.0926,-81.8335
      24246,East Stone Gap,VA,36.8668,-82.7424
      24368,Rural Retreat,VA,36.8836,-81.2879
      24374,Speedwell,VA,36.7998,-81.1834
      23090,Lightfoot,VA,37.3407,-76.7544
      24201,Bristol,VA,36.6181,-82.1823
      22401,Fredericksburg,VA,38.2995,-77.4772
      23860,Hopewell,VA,37.2876,-77.295
      24115,Martinsville,VA,36.6796,-79.8652
      23605,Newport News,VA,37.0156,-76.4332
      23510,Norfolk,VA,36.8529,-76.2878
      23805,Petersburg,VA,37.1819,-77.3854
      24009,Roanoke,VA,37.2742,-79.9579
      24157,Salem,VA,37.2884,-80.0671
      23460,Virginia Beach,VA,36.808,-76.0284
      82084,Tie Siding,WY,41.0528,-105.4462
      82321,Baggs,WY,41.0312,-107.6687
      82513,Dubois,WY,43.5451,-109.6492
      82516,Kinnear,WY,43.2678,-108.9334
      82218,Huntley,WY,41.9327,-104.1461
      20058,Washington,DC,38.8933,-77.0146
      20062,Washington,DC,38.9,-77.0369
      20075,Washington,DC,38.8933,-77.0146
      20080,Washington,DC,38.8933,-77.0146
      20223,Washington,DC,38.8933,-77.0146
      20266,Washington,DC,38.8933,-77.0146
      20317,Washington,DC,38.9363,-77.0123
      20340,Washington,DC,38.8933,-77.0146
      20409,Washington,DC,38.8933,-77.0146
      20411,Washington,DC,38.884,-77.0221
      20425,Washington,DC,38.8933,-77.0146
      20469,Washington,DC,38.8933,-77.0146
      20472,Washington,DC,38.8933,-77.0146
      20509,Washington,DC,38.8987,-77.0356
      20552,Washington,DC,38.8933,-77.0146
      20560,Washington,DC,38.8933,-77.0146
      20581,Washington,DC,38.8933,-77.0146
      56901,Washington,DC,38.8952,-77.0365
      21532,Frostburg,MD,39.6494,-78.9306
      20711,Lothian,MD,38.8029,-76.6628
      21401,Annapolis,MD,38.9898,-76.5501
      21404,Annapolis,MD,38.9784,-76.4922
      20714,North Beach,MD,38.7119,-76.5367
      20601,Waldorf,MD,38.6371,-76.8778
      20625,Cobb Island,MD,38.262,-76.8502
      20645,Issue,MD,38.2926,-76.9059
      21130,Perryman,MD,39.4716,-76.2117
      20812,Glen Echo,MD,38.9693,-77.1435
      20906,Silver Spring,MD,39.084,-77.0613
      20623,Cheltenham,MD,38.7531,-76.8369
      20740,College Park,MD,38.9963,-76.9299
      21767,Maugansville,MD,39.6996,-77.7499
      21826,Fruitland,MD,38.3225,-75.6228
      22508,Locust Grove,VA,38.3352,-77.7709
      23954,Meherrin,VA,37.1013,-78.3874
      22193,Woodbridge,VA,38.6438,-77.3451
      22802,Harrisonburg,VA,38.4905,-78.8179
      22830,Fulks Run,VA,38.6339,-78.9358
      22832,Keezletown,VA,38.4659,-78.7499
      22565,Thornburg,VA,38.1372,-77.5189
      23882,Stony Creek,VA,36.9361,-77.4443
      22610,Bentonville,VA,38.8187,-78.2757
      24313,Barren Springs,VA,36.9078,-80.809
      22333,Alexandria,VA,38.8158,-77.0901
      22906,Charlottesville,VA,38.0401,-78.4851
      23328,Chesapeake,VA,36.7085,-76.2785
      24141,Radford,VA,37.1197,-80.5565
      23278,Richmond,VA,37.5242,-77.4932
      82716,Gillette,WY,44.282,-105.4974
      82515,Hudson,WY,42.9061,-108.5834
      82605,Casper,WY,42.8666,-106.3131
      82630,Arminto,WY,43.1789,-107.2576
      82646,Natrona,WY,43.0297,-106.8092
      82842,Story,WY,44.58,-106.8978
      83013,Moran,WY,43.9509,-110.5532
      22959,North Garden,VA,37.9336,-78.6351
      24590,Scottsville,VA,37.8049,-78.474
      24521,Amherst,VA,37.6027,-79.0506
      24095,Goodview,VA,37.2123,-79.7429
      24526,Big Island,VA,37.5306,-79.3827
      23845,Ebony,VA,36.5722,-77.9888
      22535,Port Royal,VA,38.1621,-77.1837
      23976,Wylliesburg,VA,36.8391,-78.5982
      20121,Centreville,VA,38.8195,-77.4558
      22042,Falls Church,VA,38.8635,-77.1939
      22119,Merrifield,VA,38.8318,-77.2888
      22150,Springfield,VA,38.7797,-77.1866
      22181,Vienna,VA,38.8977,-77.288
      22308,Alexandria,VA,38.7316,-77.0604
      24079,Copper Hill,VA,37.0556,-80.1525
      22974,Troy,VA,37.9636,-78.2535
      24102,Henry,VA,36.8393,-79.9904
      24465,Monterey,VA,38.4058,-79.5941
      22480,Irvington,VA,37.6645,-76.416
      22507,Lively,VA,37.7572,-76.5118
      24282,Saint Charles,VA,36.8315,-83.0518
      23025,Cardinal,VA,37.4128,-76.3616
      20022,Washington,DC,38.945,-77.0364
      20214,Washington,DC,38.8933,-77.0146
      20268,Washington,DC,38.8933,-77.0146
      20299,Washington,DC,38.8933,-77.0146
      20301,Washington,DC,38.8894,-77.0311
      20410,Washington,DC,38.8933,-77.0146
      20539,Washington,DC,38.8933,-77.0146
      20590,Washington,DC,38.884,-77.0221
      56920,Washington,DC,38.8952,-77.0365
      20751,Deale,MD,38.7829,-76.5515
      21237,Rosedale,MD,39.3361,-76.5014
      21655,Preston,MD,38.7465,-75.9163
      21787,Taneytown,MD,39.6658,-77.1691
      21913,Cecilton,MD,39.4015,-75.8654
      20661,Mount Victoria,MD,38.3436,-76.8846
      21040,Edgewood,MD,39.4277,-76.3055
      21045,Columbia,MD,39.2051,-76.8322
      20815,Chevy Chase,MD,38.978,-77.082
      20868,Spencerville,MD,39.1223,-76.9722
      20878,Gaithersburg,MD,39.1125,-77.2515
      20883,Gaithersburg,MD,39.0883,-77.1568
      20896,Garrett Park,MD,39.0365,-77.0931
      20915,Silver Spring,MD,38.9907,-77.0261
      20726,Laurel,MD,39.0993,-76.8483
      20743,Capitol Heights,MD,38.8897,-76.8925
      20753,District Heights,MD,38.8336,-76.8777
      20674,Piney Point,MD,38.1397,-76.5047
      21853,Princess Anne,MD,38.1919,-75.7072
      21734,Funkstown,MD,39.609,-77.7044
      21756,Keedysville,MD,39.4563,-77.6944
      21216,Baltimore,MD,39.3093,-76.6699
      21287,Baltimore,MD,39.2975,-76.5927
      23408,Marionville,VA,37.4556,-75.8473
      22125,Occoquan,VA,38.6816,-77.2605
      22192,Woodbridge,VA,38.676,-77.3163
      24058,Belspring,VA,37.1982,-80.6151
      24179,Vinton,VA,37.2757,-79.8775
      22847,Quicksburg,VA,38.7201,-78.7213
      24311,Atkins,VA,36.8665,-81.4048
      24370,Saltville,VA,36.8927,-81.7402
      23866,Ivor,VA,36.9071,-76.8861
      22551,Spotsylvania,VA,38.162,-77.6854
      22553,Spotsylvania,VA,38.271,-77.6447
      24608,Burkes Garden,VA,37.0982,-81.3409
      22577,Sandy Point,VA,38.0673,-76.5503
      24215,Andover,VA,36.9234,-82.7964
      24450,Lexington,VA,37.7885,-79.4581
      24112,Martinsville,VA,36.6871,-79.8691
      82301,Rawlins,WY,41.7951,-107.2349
      82323,Dixon,WY,41.0445,-107.499
      82523,Pavillion,WY,43.3623,-108.6998
      82524,Saint Stephens,WY,42.9838,-108.4165
      82930,Evanston,WY,41.2609,-110.9631
      23336,Chincoteague Island,VA,37.9299,-75.3624
      23399,Jenkins Bridge,VA,37.9162,-75.6168
      23415,New Church,VA,37.9525,-75.5112
      23420,Painter,VA,37.5828,-75.8066
      22213,Arlington,VA,38.8954,-77.1633
      24019,Roanoke,VA,37.3515,-79.9052
      24554,Gladys,VA,37.1386,-79.1048
      23831,Chester,VA,37.3429,-77.4156
      22620,Boyce,VA,39.0698,-78.0203
      22714,Brandy Station,VA,38.511,-77.9037
      22726,Lignum,VA,38.3956,-77.83
      23833,Church Road,VA,37.195,-77.6646
      22504,Laneview,VA,37.7681,-76.7117
      20120,Centreville,VA,38.8448,-77.467
      20122,Centreville,VA,38.8318,-77.2888
      22043,Falls Church,VA,38.8994,-77.1895
      22303,Alexandria,VA,38.7912,-77.0814
      20137,Broad Run,VA,38.8237,-77.713
      23061,Gloucester,VA,37.4126,-76.5464
      23160,State Farm,VA,37.6454,-77.8283
      24577,Nathalie,VA,36.9356,-78.972
      23023,Bruington,VA,37.7801,-76.9402
      22719,Etlan,VA,38.5095,-78.2638
      23035,Cobbs Creek,VA,37.4959,-76.3863
      24162,Shawsville,VA,37.1466,-80.2715
      20004,Washington,DC,38.893,-77.0263
      20024,Washington,DC,38.8759,-77.016
      20067,Washington,DC,38.8933,-77.0146
      20204,Washington,DC,38.8933,-77.0146
      20241,Washington,DC,38.8933,-77.0146
      20260,Washington,DC,38.8933,-77.0146
      20265,Washington,DC,38.8933,-77.0146
      20555,Washington,DC,38.9,-77.0401
      21020,Boring,MD,39.5213,-76.8047
      21022,Brooklandville,MD,39.3979,-76.6717
      21023,Butler,MD,39.533,-76.7432
      21027,Chase,MD,39.3634,-76.3711
      20629,Dowell,MD,38.3364,-76.4524
      21048,Finksburg,MD,39.4991,-76.9101
      20664,Newburg,MD,38.3298,-76.9175
      21770,Monrovia,MD,39.3512,-77.2494
      21047,Fallston,MD,39.527,-76.4328
      21161,White Hall,MD,39.6618,-76.5666
      21723,Cooksville,MD,39.3211,-77.0051
      20810,Bethesda,MD,38.9806,-77.1008
      20818,Cabin John,MD,38.9743,-77.1591
      20830,Olney,MD,39.1552,-77.0667
      20879,Gaithersburg,MD,39.173,-77.1855
      20716,Bowie,MD,38.9263,-76.7098
      20782,Hyattsville,MD,38.9647,-76.9649
      21628,Crumpton,MD,39.233,-75.9195
      20624,Clements,MD,38.3407,-76.7264
      21679,Wye Mills,MD,38.9281,-76.0814
      21783,Smithsburg,MD,39.647,-77.5706
      21874,Willards,MD,38.3939,-75.3552
      21863,Snow Hill,MD,38.1868,-75.405
      21205,Baltimore,MD,39.3009,-76.5799
      21210,Baltimore,MD,39.3507,-76.6321
      22539,Reedville,VA,37.857,-76.2829
      23930,Crewe,VA,37.1657,-78.1059
      24053,Ararat,VA,36.6139,-80.5093
      24133,Patrick Springs,VA,36.6744,-80.1388
      23801,Fort Lee,VA,37.2447,-77.3341
      24020,Roanoke,VA,37.3589,-79.9448
      22664,Woodstock,VA,38.887,-78.5217
      22554,Stafford,VA,38.4586,-77.4306
      24606,Boissevain,VA,37.2818,-81.3804
      22642,Linden,VA,38.8812,-78.0478
      24270,Mendota,VA,36.7223,-82.2649
      24350,Ivanhoe,VA,36.8272,-80.9779
      24205,Bristol,VA,36.595,-82.1819
      22803,Harrisonburg,VA,38.4496,-78.8689
      22807,Harrisonburg,VA,38.4409,-78.8742
      23604,Fort Eustis,VA,37.1574,-76.5845
      23519,Norfolk,VA,36.9312,-76.2397
      23702,Portsmouth,VA,36.8035,-76.327
      23241,Richmond,VA,37.5242,-77.4932
      24003,Roanoke,VA,37.2742,-79.9579
      24033,Roanoke,VA,37.2742,-79.9579
      24036,Roanoke,VA,37.2742,-79.9579
      82732,Wright,WY,43.7512,-105.492
      82310,Jeffrey City,WY,42.5402,-107.8724
      82009,Cheyenne,WY,41.1836,-104.8023
      83114,Cokeville,WY,42.058,-110.9164
      83122,Grover,WY,42.7965,-110.9244
      82440,Ralston,WY,44.7161,-108.8646
      82923,Boulder,WY,42.6881,-109.5401
      23302,Assawoman,VA,37.8658,-75.5277
      23357,Greenbush,VA,37.7681,-75.6664
      24448,Iron Gate,VA,37.7965,-79.786
      23958,Pamplin,VA,37.2653,-78.6517
      24430,Craigsville,VA,38.0768,-79.3619
      24476,Steeles Tavern,VA,37.9705,-79.2233
      24412,Bacova,VA,38.054,-79.8437
      24445,Hot Springs,VA,37.9638,-79.8717
      24104,Huddleston,VA,37.1442,-79.491
      23873,Meredithville,VA,36.8088,-77.9544
      24588,Rustburg,VA,37.2545,-79.1215
      24269,Mc Clure,VA,37.0814,-82.3806
      22037,Fairfax,VA,38.8318,-77.2888
      22199,Lorton,VA,38.7043,-77.2278
      20144,Delaplane,VA,38.9113,-77.9674
      24184,Wirtz,VA,37.0818,-79.7573
      22645,Middletown,VA,39.0367,-78.2815
      24093,Glen Lyn,VA,37.3669,-80.8634
      23062,Gloucester Point,VA,37.2585,-76.4959
      23226,Richmond,VA,37.5825,-77.5197
      24442,Head Waters,VA,38.2946,-79.4353
      23085,King And Queen Court House,VA,37.6699,-76.8775
      23091,Little Plymouth,VA,37.6642,-76.8255
      22578,White Stone,VA,37.6886,-76.3616
      24265,Keokee,VA,36.8239,-82.9772
      20166,Sterling,VA,38.9814,-77.4723
      20180,Lovettsville,VA,39.2204,-77.6596
      22731,Pratts,VA,38.3366,-78.2573
      23175,Urbanna,VA,37.6356,-76.5918
      24062,Blacksburg,VA,37.2296,-80.4139
      20765,Galesville,MD,38.8446,-76.5456
      21226,Curtis Bay,MD,39.2109,-76.5597
      20754,Dunkirk,MD,38.7408,-76.6427
      20604,Waldorf,MD,38.5095,-76.9817
      21702,Frederick,MD,39.4926,-77.4612
      21714,Braddock Heights,MD,39.4203,-77.5051
      21718,Burkittsville,MD,39.3923,-77.6275
      21650,Massey,MD,39.3126,-75.8215
      20827,Bethesda,MD,39.144,-77.2076
      20866,Burtonsville,MD,39.0922,-76.9339
      20874,Germantown,MD,39.1355,-77.2822
      20902,Silver Spring,MD,39.04,-77.0444
      20746,Suitland,MD,38.8425,-76.9222
      20757,Temple Hills,MD,38.814,-76.9455
      20792,Upper Marlboro,MD,38.8158,-76.75
      21656,Price,MD,39.0346,-76.0921
      20660,Morganza,MD,38.3754,-76.6955
      21711,Big Pool,MD,39.6457,-78.0104
      21720,Cavetown,MD,39.6473,-77.5842
      21843,Ocean City,MD,38.3365,-75.0849
      21872,Whaleyville,MD,38.4121,-75.2811
      21229,Baltimore,MD,39.2856,-76.6899
      21235,Baltimore,MD,39.2847,-76.6205
      20755,Fort George G Meade,MD,39.1059,-76.7467
      21032,Crownsville,MD,39.0489,-76.5935
      20217,Washington,DC,38.8933,-77.0146
      20310,Washington,DC,38.8933,-77.0146
      20374,Washington Navy Yard,DC,38.8951,-77.0364
      20576,Washington,DC,38.8937,-77.0236
      20578,Washington,DC,38.8933,-77.0146
      23307,Birdsnest,VA,37.439,-75.881
      24471,Port Republic,VA,38.3226,-78.7886
      24377,Tannersville,VA,36.9763,-81.628
      24622,Jewell Ridge,VA,37.2094,-81.7956
      24216,Appalachia,VA,36.9438,-82.7976
      24322,Cripple Creek,VA,36.8086,-81.1039
      22301,Alexandria,VA,38.82,-77.0589
      23609,Newport News,VA,37.1959,-76.5248
      23523,Norfolk,VA,36.8294,-76.2701
      23541,Norfolk,VA,36.9312,-76.2397
      23708,Portsmouth,VA,36.8354,-76.2983
      24142,Radford,VA,37.1387,-80.548
      23260,Richmond,VA,37.5538,-77.4603
      23269,Richmond,VA,37.5538,-77.4603
      24004,Roanoke,VA,37.2742,-79.9579
      24042,Roanoke,VA,37.2717,-79.9392
      23450,Virginia Beach,VA,36.844,-76.1204
      23465,Virginia Beach,VA,36.8512,-76.1692
      82063,Jelm,WY,41.0578,-106.0133
      82411,Burlington,WY,44.4442,-108.4327
      82725,Recluse,WY,44.7861,-105.776
      82727,Rozet,WY,44.3058,-105.2459
      82224,Lost Springs,WY,43.0421,-104.7973
      82834,Buffalo,WY,44.3485,-106.7073
      82840,Saddlestring,WY,44.4976,-106.871
      83110,Afton,WY,42.7128,-110.942
      83116,Diamondville,WY,41.7839,-110.54
      82433,Meeteetse,WY,44.1962,-108.95
      82201,Wheatland,WY,42.0495,-104.9679
      82833,Big Horn,WY,44.6531,-107.0247
      82837,Leiter,WY,44.718,-106.2692
      83113,Big Piney,WY,42.6483,-110.1246
      82442,Ten Sleep,WY,43.9978,-107.4153
      24457,Low Moor,VA,37.7887,-79.8837
      22219,Arlington,VA,38.8808,-77.1129
      22245,Arlington,VA,38.8518,-77.0523
      24485,West Augusta,VA,38.2744,-79.3201
      24325,Dugspur,VA,36.8145,-80.6123
      23236,Richmond,VA,37.4782,-77.5854
      22724,Jeffersonton,VA,38.6256,-77.9069
      24226,Clinchco,VA,37.1406,-82.3425
      20124,Clifton,VA,38.7818,-77.3818
      22003,Annandale,VA,38.8307,-77.2142
      22060,Fort Belvoir,VA,38.6947,-77.1433
      22965,Quinque,VA,38.3301,-78.475
      23898,Zuni,VA,36.8437,-76.811
      22448,Dahlgren,VA,38.3375,-77.0429
      22523,Morattico,VA,37.7928,-76.6093
      20129,Paeonian Springs,VA,39.1603,-77.6097
      20147,Ashburn,VA,39.0373,-77.4805
      20152,Chantilly,VA,38.8976,-77.5092
      20005,Washington,DC,38.9067,-77.0312
      20056,Washington,DC,38.8933,-77.0146
      20242,Washington,DC,38.8678,-77.0289
      20306,Washington,DC,38.8933,-77.0146
      20510,Washington,DC,38.8933,-77.0146
      20523,Washington,DC,38.8945,-77.0478
      20525,Washington,DC,38.8933,-77.0146
      20527,Washington,DC,38.9029,-77.0361
      20534,Washington,DC,38.8941,-77.0125
      20536,Washington,DC,38.9012,-77.0169
      20570,Washington,DC,38.8991,-77.0401
      56915,Washington,DC,38.8952,-77.0365
      21077,Harmans,MD,39.1561,-76.6977
      21144,Severn,MD,39.1275,-76.698
      21051,Fork,MD,39.4731,-76.4484
      21133,Randallstown,MD,39.3746,-76.8002
      21250,Baltimore,MD,39.2582,-76.7131
      21282,Pikesville,MD,39.3743,-76.7225
      21632,Federalsburg,MD,38.7147,-75.7754
      21649,Marydel,MD,39.1082,-75.7622
      21074,Hampstead,MD,39.6146,-76.8644
      21158,Westminster,MD,39.607,-77.0294
      21776,New Windsor,MD,39.5162,-77.1034
      20611,Bel Alton,MD,38.4731,-76.9789
      20616,Bryans Road,MD,38.6415,-77.0766
      20682,Rock Point,MD,38.2836,-76.8481
      21780,Sabillasville,MD,39.6828,-77.4693
      20588,Dhs,MD,38.7504,-76.8544
      21651,Millington,MD,39.2743,-75.8951
      21667,Still Pond,MD,39.3293,-76.0455
      20847,Rockville,MD,39.084,-77.1528
      20911,Silver Spring,MD,38.9907,-77.0261
      20768,Greenbelt,MD,39.0046,-76.8755
      20790,Capitol Heights,MD,38.8336,-76.8777
      21666,Stevensville,MD,38.9394,-76.3371
      21867,Upper Fairmount,MD,38.104,-75.7913
      21801,Salisbury,MD,38.3824,-75.6336
      21802,Salisbury,MD,38.3884,-75.6276
      21856,Quantico,MD,38.3339,-75.7851
      21239,Baltimore,MD,39.361,-76.5891
      21241,Baltimore,MD,39.2847,-76.6205
      21545,Mount Savage,MD,39.6991,-78.8739
      20758,Friendship,MD,38.7361,-76.5835
      21035,Davidsonville,MD,38.9374,-76.6375
      24549,Dry Fork,VA,36.743,-79.458
      22472,Haynesville,VA,37.9505,-76.6367
      22834,Linville,VA,38.5557,-78.8961
      24225,Cleveland,VA,36.9502,-82.1318
      22626,Fishers Hill,VA,38.9834,-78.4038
      24640,Red Ash,VA,37.1134,-81.871
      23851,Franklin,VA,36.6786,-76.9391
      23664,Hampton,VA,37.0566,-76.2966
      23668,Hampton,VA,37.0206,-76.3377
      24514,Lynchburg,VA,37.4009,-79.1785
      20113,Manassas,VA,38.7709,-77.4494
      23511,Norfolk,VA,36.9356,-76.3034
      23223,Richmond,VA,37.5477,-77.3948
      23232,Richmond,VA,37.5202,-77.4084
      23291,Richmond,VA,37.5242,-77.4932
      24005,Roanoke,VA,37.2742,-79.9579
      24025,Roanoke,VA,37.2742,-79.9579
      23467,Virginia Beach,VA,36.7957,-76.0126
      82052,Buford,WY,41.1219,-105.3047
      82426,Greybull,WY,44.4919,-108.0795
      82642,Lysite,WY,43.3284,-107.6488
      83101,Kemmerer,WY,41.7887,-110.5283
      82609,Casper,WY,42.8406,-106.2806
      82835,Clearmont,WY,44.661,-106.4581
      82942,Point Of Rocks,WY,41.6802,-108.7857
      23401,Keller,VA,37.6193,-75.7638
      23427,Saxis,VA,37.9264,-75.722
      22987,White Hall,VA,38.1179,-78.6614
      24562,Howardsville,VA,37.7511,-78.6313
      24460,Millboro,VA,38.0553,-79.7464
      23876,Rawlings,VA,36.953,-77.8237
      23887,Valentines,VA,36.565,-77.8384
      24647,Shortt Gap,VA,37.1573,-81.8729
      22501,Ladysmith,VA,38.0176,-77.5153
      24351,Lambsburg,VA,36.5775,-80.7601
      23923,Charlotte Court House,VA,37.0879,-78.6378
      23894,Wilsons,VA,37.1157,-77.8352
      24092,Glade Hill,VA,36.9859,-79.773
      22637,Gore,VA,39.2887,-78.3417
      23003,Ark,VA,37.4323,-76.6016
      23102,Maidens,VA,37.7126,-77.8315
      24330,Fries,VA,36.7247,-81.0042
      24558,Halifax,VA,36.7626,-78.9415
      23424,Rescue,VA,36.9969,-76.5645
      23108,Mascot,VA,37.6271,-76.7072
      23093,Louisa,VA,38.0132,-78.0347
      23109,Mathews,VA,37.4383,-76.3544
      23163,Susan,VA,37.3508,-76.3161
      23043,Deltaville,VA,37.5549,-76.3468
      23176,Wake,VA,37.5681,-76.4323
      24060,Blacksburg,VA,37.2563,-80.4347
      24138,Pilot,VA,37.0565,-80.3229
      23422,Pungoteague,VA,37.6304,-75.8135
      22943,Greenwood,VA,38.0415,-78.7833
      22206,Arlington,VA,38.8415,-77.0905
      24459,Middlebrook,VA,38.0242,-79.2812
      24479,Swoope,VA,38.1591,-79.1873
      24064,Blue Ridge,VA,37.3885,-79.8172
      24628,Maxie,VA,37.3012,-82.1743
      24631,Oakwood,VA,37.2138,-81.9917
      24656,Vansant,VA,37.1738,-82.1277
      24256,Haysi,VA,37.2206,-82.2853
      22438,Champlain,VA,38.0214,-76.9719
      22015,Burke,VA,38.7894,-77.2818
      22151,Springfield,VA,38.8029,-77.2116
      24348,Independence,VA,36.6294,-81.1582
      23111,Mechanicsville,VA,37.6281,-77.3395
      23255,Henrico,VA,37.4638,-77.398
      24089,Fieldale,VA,36.7064,-79.9652
      22544,Rollins Fork,VA,38.1849,-77.0622
      20132,Purcellville,VA,39.1436,-77.7342
      22723,Hood,VA,38.3549,-78.3828
      22748,Wolftown,VA,38.3557,-78.3475
      23066,Gwynn,VA,37.5043,-76.2886
      24023,Roanoke,VA,37.271,-79.9414
      24068,Christiansburg,VA,37.1548,-80.4184
      21528,Eckhart Mines,MD,39.6528,-78.9014
      20764,Shady Side,MD,38.8368,-76.5109
      23139,Powhatan,VA,37.5421,-77.9189
      22172,Triangle,VA,38.5509,-77.3229
      24441,Grottoes,VA,38.2484,-78.8255
      22555,Stafford,VA,38.4221,-77.4083
      23846,Elberon,VA,37.0701,-76.8337
      24212,Abingdon,VA,36.6909,-81.9708
      23320,Chesapeake,VA,36.7352,-76.2384
      23834,Colonial Heights,VA,37.27,-77.4038
      23513,Norfolk,VA,36.8914,-76.2396
      23249,Richmond,VA,37.5242,-77.4932
      24017,Roanoke,VA,37.2937,-79.9902
      24031,Roanoke,VA,37.2742,-79.9579
      82324,Elk Mountain,WY,41.6874,-106.4146
      82720,Hulett,WY,44.7352,-104.6174
      82060,Hillsdale,WY,41.2136,-104.4933
      82604,Casper,WY,42.8261,-106.3896
      22931,Covesville,VA,37.908,-78.7127
      24422,Clifton Forge,VA,37.8203,-79.8054
      22217,Arlington,VA,38.8808,-77.1129
      22246,Arlington,VA,38.8808,-77.1129
      24314,Bastian,VA,37.1574,-81.1989
      24318,Ceres,VA,37.0046,-81.3643
      23004,Arvonia,VA,37.6714,-78.3889
      24528,Brookneal,VA,37.0827,-78.9227
      23836,Chester,VA,37.3475,-77.3386
      24228,Clintwood,VA,37.1592,-82.4453
      23885,Sutherland,VA,37.1901,-77.5655
      22436,Caret,VA,37.9826,-76.9614
      22315,Alexandria,VA,38.7596,-77.1485
      22603,Winchester,VA,39.264,-78.1989
      24167,Staffordsville,VA,37.2491,-80.7344
      24363,Mouth Of Wilson,VA,36.6104,-81.3955
      24535,Cluster Springs,VA,36.6113,-78.9455
      20105,Aldie,VA,38.9577,-77.6038
      20149,Ashburn,VA,39.0437,-77.4875
      22730,Oakpark,VA,38.3674,-78.1628
      20003,Washington,DC,38.8829,-76.9895
      20018,Washington,DC,38.9277,-76.9762
      20027,Washington,DC,38.9007,-77.0501
      20036,Washington,DC,38.9087,-77.0414
      20049,Washington,DC,38.8959,-77.021
      20055,Washington,DC,38.9016,-77.021
      20069,Washington,DC,38.8933,-77.0146
      20203,Washington,DC,38.9053,-77.0466
      20262,Washington,DC,38.8933,-77.0146
      20444,Washington,DC,38.8933,-77.0146
      20453,Washington,DC,38.8933,-77.0146
      20456,Washington,DC,38.8981,-77.0401
      21061,Glen Burnie,MD,39.1618,-76.6297
      21155,Upperco,MD,39.5676,-76.7972
      21284,Towson,MD,39.4015,-76.6019
      20662,Nanjemoy,MD,38.4462,-77.1983
      21643,Hurlock,MD,38.6438,-75.863
      21792,Unionville,MD,39.4748,-77.1855
      21522,Bittinger,MD,39.6023,-79.2234
      21005,Aberdeen Proving Ground,MD,39.4771,-76.1208
      21765,Lisbon,MD,39.3378,-77.072
      21620,Chestertown,MD,39.2125,-76.0802
      20738,Riverdale,MD,38.8336,-76.8777
      21657,Queen Anne,MD,38.9456,-75.9777
      21214,Baltimore,MD,39.3521,-76.5644
      21230,Baltimore,MD,39.2645,-76.6224
      21529,Ellerslie,MD,39.7083,-78.7774
      21555,Oldtown,MD,39.5846,-78.6044
      20020,Washington,DC,38.86,-76.9742
      20068,Washington,DC,38.8951,-77.0364
      20211,Washington,DC,38.8933,-77.0146
      20221,Washington,DC,38.8933,-77.0146
      20226,Washington,DC,38.8933,-77.0146
      20376,Washington Navy Yard,DC,38.9164,-76.9947
      20393,Washington,DC,38.8933,-77.0146
      20416,Washington,DC,38.8933,-77.0146
      20526,Washington,DC,38.9022,-77.0437
      20549,Washington,DC,38.8933,-77.0146
      21076,Hanover,MD,39.1551,-76.7215
      21131,Phoenix,MD,39.4833,-76.5776
      21221,Essex,MD,39.3086,-76.4533
      20732,Chesapeake Beach,MD,38.6698,-76.5376
      21629,Denton,MD,38.8595,-75.8357
      21627,Crocheron,MD,38.2426,-76.0531
      21520,Accident,MD,39.6355,-79.3085
      21036,Dayton,MD,39.2339,-76.9968
      21075,Elkridge,MD,39.2058,-76.7531
      20816,Bethesda,MD,38.9585,-77.1153
      20825,Chevy Chase,MD,39.0029,-77.0712
      20833,Brookeville,MD,39.1871,-77.0603
      20899,Gaithersburg,MD,39.1403,-77.222
      20722,Brentwood,MD,38.9407,-76.9531
      20752,Suitland,MD,38.8487,-76.9239
      20627,Compton,MD,38.2768,-76.704
      21653,Newcomb,MD,38.7518,-76.178
      21749,Hagerstown,MD,39.6418,-77.72
      21803,Salisbury,MD,38.3884,-75.6276
      21811,Berlin,MD,38.3475,-75.1866
      21233,Baltimore,MD,39.2847,-76.6205
      21280,Baltimore,MD,39.2847,-76.6205
      24599,Wingina,VA,37.6399,-78.7239
      22473,Heathsville,VA,37.9072,-76.4178
      24171,Stuart,VA,36.6517,-80.2392
      22811,Bergton,VA,38.7925,-78.9668
      22850,Singers Glen,VA,38.5574,-78.9227
      24245,Dungannon,VA,36.8242,-82.496
      22810,Basye,VA,38.8089,-78.7776
      22545,Ruby,VA,38.5086,-77.543
      22556,Stafford,VA,38.4718,-77.5102
      24601,Amonate,VA,37.1909,-81.6387
      24641,Richlands,VA,37.0941,-81.8123
      23323,Chesapeake,VA,36.7634,-76.3397
      24426,Covington,VA,37.7802,-79.987
      24543,Danville,VA,36.5927,-79.411
      23847,Emporia,VA,36.6857,-77.563
      23661,Hampton,VA,37.0074,-76.3801
      24016,Roanoke,VA,37.2704,-79.9535
      24026,Roanoke,VA,37.2742,-79.9579
      24040,Roanoke,VA,37.2742,-79.9579
      24155,Salem,VA,37.2884,-80.0671
      23461,Virginia Beach,VA,36.7754,-75.9633
      82329,Medicine Bow,WY,41.903,-106.2012
      82082,Pine Bluffs,WY,41.1788,-104.0666
      82620,Alcova,WY,42.5302,-106.76
      83001,Jackson,WY,43.4528,-110.7393
      21108,Millersville,MD,39.1041,-76.619
      21113,Odenton,MD,39.0762,-76.6996
      21136,Reisterstown,MD,39.46,-76.8135
      21220,Middle River,MD,39.3401,-76.4153
      21234,Parkville,MD,39.3876,-76.5418
      21251,Baltimore,MD,39.439,-76.5921
      21285,Towson,MD,39.4015,-76.6019
      21641,Hillsboro,MD,38.9206,-75.9388
      21157,Westminster,MD,39.5642,-76.9807
      21634,Fishing Creek,MD,38.3223,-76.2244
      21710,Adamstown,MD,39.291,-77.4552
      21717,Buckeystown,MD,39.3309,-77.4274
      21773,Myersville,MD,39.5282,-77.5513
      21536,Grantsville,MD,39.6551,-79.1241
      20701,Annapolis Junction,MD,39.1332,-76.7988
      20817,Bethesda,MD,38.9896,-77.1538
      20832,Olney,MD,39.1526,-77.0749
      20839,Beallsville,MD,39.179,-77.4128
      20842,Dickerson,MD,39.2126,-77.4199
      20855,Derwood,MD,39.1345,-77.1477
      20889,Bethesda,MD,39.144,-77.2076
      20895,Kensington,MD,39.0298,-77.0793
      20898,Gaithersburg,MD,39.144,-77.2076
      20636,Hollywood,MD,38.3524,-76.5626
      21842,Ocean City,MD,38.3365,-75.0849
      21862,Showell,MD,38.4003,-75.2166
      21264,Baltimore,MD,39.2847,-76.6205
      21281,Baltimore,MD,39.2847,-76.6205
      23398,Jamesville,VA,37.5151,-75.9305
      24594,Sutherlin,VA,36.6258,-79.195
      20169,Haymarket,VA,38.8674,-77.6445
      22460,Farnham,VA,37.874,-76.605
      24280,Rosedale,VA,36.9593,-81.9312
      24258,Hiltons,VA,36.6498,-82.4299
      22408,Fredericksburg,VA,38.2481,-77.4681
      24604,Bishop,VA,37.2033,-81.5575
      24630,North Tazewell,VA,37.1756,-81.5399
      23651,Fort Monroe,VA,37.018,-76.3044
      23665,Hampton,VA,37.0831,-76.36
      23503,Norfolk,VA,36.9442,-76.252
      23219,Richmond,VA,37.5463,-77.4378
      23290,Richmond,VA,37.5242,-77.4932
      23298,Richmond,VA,37.5406,-77.4316
      24027,Roanoke,VA,37.2742,-79.9579
      23454,Virginia Beach,VA,36.8282,-76.0237
      23466,Virginia Beach,VA,36.7957,-76.0126
      82421,Deaver,WY,44.9257,-108.5979
      82431,Lovell,WY,44.8336,-108.4141
      82637,Glenrock,WY,42.7803,-105.8719
      82061,Horse Creek,WY,41.4353,-105.1417
      82934,Granger,WY,41.5936,-109.9688
      83014,Wilson,WY,43.4992,-110.8742
      24522,Appomattox,VA,37.353,-78.8224
      22214,Arlington,VA,38.8688,-77.0739
      24477,Stuarts Draft,VA,38.0144,-79.0298
      24122,Montvale,VA,37.407,-79.7175
      24174,Thaxton,VA,37.36,-79.6522
      24366,Rocky Gap,VA,37.2578,-81.1159
      22427,Bowling Green,VA,38.0137,-77.1802
      23962,Randolph,VA,36.9515,-78.7054
      22729,Mitchells,VA,38.3744,-78.0105
      23841,Dinwiddie,VA,37.0833,-77.5585
      22437,Center Cross,VA,37.7929,-76.7548
      22454,Dunnsville,VA,37.8527,-76.8475
      20151,Chantilly,VA,38.8867,-77.4457
      22121,Mount Vernon,VA,38.7192,-77.1073
      22124,Oakton,VA,38.8852,-77.3233
      20188,Warrenton,VA,38.7656,-77.8203
      24134,Pearisburg,VA,37.3041,-80.7267
      23129,Oilville,VA,37.704,-77.7853
      24589,Scottsburg,VA,36.7862,-78.7866
      23250,Richmond,VA,37.5075,-77.3329
      22526,Ninde,VA,38.2712,-77.0561
      23128,North,VA,37.4401,-76.4238
      23924,Chase City,VA,36.8053,-78.4553
      23169,Topping,VA,37.6009,-76.4575
      24063,Blacksburg,VA,37.2296,-80.4139
      22971,Shipman,VA,37.76,-78.8105
      20015,Washington,DC,38.9658,-77.068
      20019,Washington,DC,38.8902,-76.9376
      20043,Washington,DC,38.8933,-77.0146
      20073,Washington,DC,38.897,-77.0251
      20427,Washington,DC,38.9021,-77.0476
      20435,Washington,DC,38.8994,-77.0403
      21502,Cumberland,MD,39.5992,-78.8444
      23310,Cape Charles,VA,37.2799,-75.9721
      23350,Exmore,VA,37.5117,-75.8526
      22433,Burr Hill,VA,38.3446,-77.8719
      20156,Gainesville,VA,38.7219,-77.4669
      20168,Haymarket,VA,38.8121,-77.6364
      22749,Woodville,VA,38.6482,-78.1739
      24435,Fairfield,VA,37.8778,-79.2979
      22407,Fredericksburg,VA,38.2688,-77.5476
      23899,Claremont,VA,37.2279,-76.9641
      23888,Wakefield,VA,36.9757,-76.979
      24210,Abingdon,VA,36.6916,-82.02
      22320,Alexandria,VA,38.8044,-77.0467
      23326,Chesapeake,VA,36.777,-76.2394
      22040,Falls Church,VA,38.8842,-77.1718
      23681,Hampton,VA,37.0727,-76.3899
      23225,Richmond,VA,37.5158,-77.5047
      23279,Richmond,VA,37.5242,-77.4932
      82501,Riverton,WY,43.0351,-108.2024
      82649,Shoshoni,WY,43.2457,-108.1007
      82059,Granite Canon,WY,41.0473,-105.1517
      83118,Etna,WY,43.0443,-111.0085
      83119,Fairview,WY,42.6885,-110.9824
      82638,Hiland,WY,43.1153,-107.3436
      82190,Yellowstone National Park,WY,44.7957,-110.6137
      82801,Sheridan,WY,44.7849,-106.9648
      23303,Atlantic,VA,37.9073,-75.5089
      23341,Craddockville,VA,37.5773,-75.8646
      23395,Horntown,VA,37.9699,-75.4716
      22939,Fishersville,VA,38.0964,-78.9929
      24432,Deerfield,VA,38.1842,-79.4152
      24077,Cloverdale,VA,37.3651,-79.9056
      23857,Gasburg,VA,36.5856,-77.8836
      23889,Warfield,VA,36.9011,-77.7673
      24634,Pilgrims Knob,VA,37.2746,-81.9024
      23235,Richmond,VA,37.5133,-77.5646
      22713,Boston,VA,38.5382,-78.1423
      22039,Fairfax Station,VA,38.7602,-77.3064
      22180,Vienna,VA,38.8935,-77.2532
      20186,Warrenton,VA,38.6898,-77.8361
      22720,Goldvein,VA,38.4725,-77.6423
      24151,Rocky Mount,VA,36.9891,-79.884
      22625,Cross Junction,VA,39.3744,-78.3081
      24147,Rich Creek,VA,37.385,-80.8227
      23870,Jarratt,VA,36.8143,-77.4683
      23192,Montpelier,VA,37.8177,-77.6924
      23288,Henrico,VA,37.4638,-77.398
      22451,Dogue,VA,38.2321,-77.2158
      20101,Dulles,VA,39.0021,-77.4421
      20103,Dulles,VA,38.9962,-77.45
      23045,Diggs,VA,37.4296,-76.2629
      23070,Hardyville,VA,37.5512,-76.3844
      21146,Severna Park,MD,39.0811,-76.5577
      21244,Windsor Mill,MD,39.3331,-76.7849
      21286,Towson,MD,39.4143,-76.5761
      20615,Broomes Island,MD,38.418,-76.5478
      20657,Lusby,MD,38.3661,-76.4346
      20689,Sunderland,MD,38.649,-76.5767
      20632,Faulkner,MD,38.4382,-76.9729
      21775,New Midway,MD,39.5645,-77.2947
      20763,Savage,MD,39.138,-76.8218
      20794,Jessup,MD,39.1484,-76.7922
      20854,Potomac,MD,39.0388,-77.1922
      20862,Brinklow,MD,39.1838,-77.0163
      20872,Damascus,MD,39.2761,-77.2131
      20608,Aquasco,MD,38.5825,-76.7149
      20721,Bowie,MD,38.9194,-76.7871
      20769,Glenn Dale,MD,38.9766,-76.8053
      20770,Greenbelt,MD,38.9996,-76.884
      21617,Centreville,MD,39.0564,-76.045
      21690,Chestertown,MD,39.0346,-76.0921
      20606,Abell,MD,38.2471,-76.7481
      20684,Saint Inigoes,MD,38.1441,-76.4083
      21817,Crisfield,MD,37.9845,-75.8429
      21662,Royal Oak,MD,38.714,-76.2097
      20066,Washington,DC,38.8933,-77.0146
      20070,Washington,DC,38.8933,-77.0146
      20077,Washington,DC,38.8933,-77.0146
      20252,Washington,DC,38.9584,-77.0519
      20380,Washington,DC,38.8933,-77.0146
      20428,Washington,DC,38.8933,-77.0146
      20504,Washington,DC,38.8933,-77.0146
      20515,Washington,DC,38.8933,-77.0146
      22432,Burgess,VA,37.8687,-76.3542
      22627,Flint Hill,VA,38.7629,-78.1
      24224,Castlewood,VA,36.8764,-82.2876
      22442,Coles Point,VA,38.1437,-76.6355
      24293,Wise,VA,36.975,-82.5947
      23327,Chesapeake,VA,36.7085,-76.2785
      22404,Fredericksburg,VA,38.3032,-77.4605
      23666,Hampton,VA,37.0462,-76.4096
      20109,Manassas,VA,38.793,-77.5266
      23607,Newport News,VA,36.9864,-76.4165
      23709,Portsmouth,VA,36.8686,-76.3552
      24002,Roanoke,VA,37.2742,-79.9579
      24034,Roanoke,VA,37.2742,-79.9579
      24153,Salem,VA,37.2853,-80.0692
      23457,Virginia Beach,VA,36.6224,-76.0249
      82070,Laramie,WY,41.439,-105.801
      82072,Laramie,WY,41.4247,-105.4781
      82718,Gillette,WY,43.9282,-105.5492
      82332,Savery,WY,41.0395,-107.4234
      82219,Jay Em,WY,42.4987,-104.5134
      82008,Cheyenne,WY,41.14,-104.8202
      82601,Casper,WY,42.8458,-106.3166
      23356,Greenbackville,VA,38.0064,-75.4029
      22911,Charlottesville,VA,38.0995,-78.4085
      22216,Arlington,VA,38.8808,-77.1129
      22230,Arlington,VA,38.8797,-77.1108
      24484,Warm Springs,VA,38.1437,-79.8207
      24551,Forest,VA,37.3379,-79.2791
      24050,Roanoke,VA,37.5551,-79.7862
      23959,Phenix,VA,37.0925,-78.7912
      24607,Breaks,VA,37.2959,-82.281
      22033,Fairfax,VA,38.8776,-77.3885
      22103,West Mclean,VA,38.8318,-77.2888
      22122,Newington,VA,38.7384,-77.185
      22183,Vienna,VA,38.8318,-77.2888
      20185,Upperville,VA,38.993,-77.8799
      22655,Stephens City,VA,39.0834,-78.1907
      23014,Beaumont,VA,37.7338,-77.8881
      23116,Mechanicsville,VA,37.6691,-77.3294
      23126,Newtown,VA,37.9226,-77.1449
      23119,Moon,VA,37.4487,-76.2766
      23079,Jamaica,VA,37.73,-76.689
      21123,Pasadena,MD,38.9742,-76.5949
      21128,Perry Hall,MD,39.401,-76.451
      21670,Templeville,MD,39.1362,-75.766
      21672,Toddville,MD,38.2726,-76.0596
      21771,Mount Airy,MD,39.3881,-77.1723
      21550,Oakland,MD,39.4339,-79.3167
      21015,Bel Air,MD,39.5303,-76.3153
      21661,Rock Hall,MD,39.1344,-76.2305
      20813,Bethesda,MD,39.144,-77.2076
      20897,Suburb Maryland Fac,MD,39.144,-77.2076
      20613,Brandywine,MD,38.6922,-76.832
      20741,College Park,MD,38.8336,-76.8777
      20788,Hyattsville,MD,38.9694,-76.9509
      20626,Coltons Point,MD,38.237,-76.7646
      20634,Great Mills,MD,38.2674,-76.4954
      21824,Ewell,MD,37.9938,-76.0351
      21741,Hagerstown,MD,39.6418,-77.72
      21779,Rohrersville,MD,39.4431,-77.658
      21810,Allen,MD,38.2873,-75.688
      20010,Washington,DC,38.9327,-77.0322
      20082,Washington,DC,38.8933,-77.0146
      20233,Washington,DC,38.8933,-77.0146
      20395,Washington,DC,38.8933,-77.0146
      20507,Washington,DC,38.8933,-77.0146
      20541,Washington,DC,38.8874,-77.0047
      20547,Washington,DC,38.8933,-77.0146
      20554,Washington,DC,38.8933,-77.0146
      21557,Rawlings,MD,39.5214,-78.9062
      23316,Cheriton,VA,37.2896,-75.9713
      22650,Rileyville,VA,38.7564,-78.3873
      23891,Waverly,VA,37.0508,-77.2124
      24602,Bandy,VA,37.1661,-81.6508
      22520,Montross,VA,38.1105,-76.7828
      22529,Oldhams,VA,38.0181,-76.6861
      22314,Alexandria,VA,38.806,-77.0529
      24416,Buena Vista,VA,37.7396,-79.3523
      20112,Manassas,VA,38.6665,-77.4248
      23612,Newport News,VA,37.1959,-76.5248
      23505,Norfolk,VA,36.9168,-76.2875
      23551,Norfolk,VA,36.9312,-76.2397
      23273,Henrico,VA,37.5302,-77.4753
      24008,Roanoke,VA,37.2742,-79.9579
      24010,Roanoke,VA,37.2742,-79.9579
      24030,Roanoke,VA,37.2742,-79.9579
      82335,Walcott,WY,41.7611,-106.845
      82636,Evansville,WY,42.8992,-106.1754
      82943,Reliance,WY,41.6698,-109.1919
      82939,Mountain View,WY,41.2335,-110.3372
      82715,Four Corners,WY,44.0775,-104.1383
      20026,Washington,DC,38.8933,-77.0146
      20047,Washington,DC,38.8933,-77.0146
      20064,Washington,DC,38.9332,-76.9963
      20078,Washington,DC,38.8933,-77.0146
      20250,Washington,DC,38.8873,-77.0327
      20403,Washington,DC,38.8933,-77.0146
      20502,Washington,DC,38.8987,-77.0362
      20537,Washington,DC,38.8941,-77.0251
      20544,Washington,DC,38.8933,-77.0146
      20594,Washington,DC,38.8849,-77.0184
      23410,Melfa,VA,37.6407,-75.7454
      22937,Esmont,VA,37.8125,-78.6106
      24556,Goode,VA,37.3755,-79.4039
      24570,Lowry,VA,37.3484,-79.4271
      24315,Bland,VA,37.1376,-81.0201
      24517,Altavista,VA,37.1222,-79.2911
      22108,Mc Lean,VA,38.9321,-77.1841
      20130,Paris,VA,39.0048,-77.9546
      20198,The Plains,VA,38.8707,-77.7608
      24091,Floyd,VA,36.8957,-80.3275
      23084,Kents Store,VA,37.8942,-78.1208
      23183,White Marsh,VA,37.3632,-76.5325
      20160,Lincoln,VA,39.0985,-77.6883
      23170,Trevilians,VA,38.0515,-78.0728
      22938,Faber,VA,37.8475,-78.7565
      22949,Lovingston,VA,37.7924,-78.8684
      22976,Tyro,VA,37.8391,-79.0693
      21503,Cumberland,MD,39.6529,-78.7625
      20776,Harwood,MD,38.8582,-76.6145
      21037,Edgewater,MD,38.9149,-76.5424
      21056,Gibson Island,MD,39.0751,-76.4324
      21162,White Marsh,MD,39.3923,-76.4132
      20736,Owings,MD,38.6955,-76.6061
      21626,Crapo,MD,38.3295,-76.1142
      21561,Swanton,MD,39.4764,-79.2402
      21010,Gunpowder,MD,39.3982,-76.2743
      21014,Bel Air,MD,39.5394,-76.3564
      21043,Ellicott City,MD,39.2548,-76.8001
      21150,Simpsonville,MD,39.2364,-76.9419
      20851,Rockville,MD,39.0763,-77.1234
      20852,Rockville,MD,39.0496,-77.1204
      20774,Upper Marlboro,MD,38.8682,-76.8156
      20781,Hyattsville,MD,38.9506,-76.9347
      21607,Barclay,MD,39.1299,-75.8601
      21638,Grasonville,MD,38.9456,-76.1997
      20659,Mechanicsville,MD,38.4293,-76.7254
      21652,Neavitt,MD,38.7246,-76.2824
      21715,Brownsville,MD,39.3869,-77.658
      21742,Hagerstown,MD,39.6573,-77.6921
      21840,Nanticoke,MD,38.2672,-75.9021
      21213,Baltimore,MD,39.3127,-76.581
      20006,Washington,DC,38.8964,-77.0447
      20008,Washington,DC,38.9363,-77.0599
      20033,Washington,DC,38.8933,-77.0146
      20042,Washington,DC,38.8933,-77.0146
      20063,Washington,DC,38.9053,-77.0466
      20212,Washington,DC,38.8933,-77.0146
      20235,Washington,DC,38.9154,-77.0572
      20401,Washington,DC,38.8933,-77.0146
      20500,Washington,DC,38.8946,-77.0355
      23011,Barhamsville,VA,37.4617,-76.8321
      24530,Callands,VA,36.7647,-79.6288
      23842,Disputanta,VA,37.1483,-77.2732
      20182,Nokesville,VA,38.7009,-77.5857
      22134,Quantico,VA,38.531,-77.3358
      22548,Sharps,VA,37.824,-76.7008
      24271,Nickelsville,VA,36.7502,-82.4202
      23890,Waverly,VA,37.025,-77.1055
      24619,Horsepen,VA,37.1355,-81.5634
      22488,Kinsale,VA,38.0505,-76.5855
      24312,Austinville,VA,36.8195,-80.8583
      24382,Wytheville,VA,36.9407,-81.0941
      22313,Alexandria,VA,38.8158,-77.0901
      22907,Charlottesville,VA,38.0401,-78.4851
      22908,Charlottesville,VA,38.0401,-78.4851
      22046,Falls Church,VA,38.8856,-77.1802
      24114,Martinsville,VA,36.6796,-79.8652
      23603,Newport News,VA,37.1989,-76.5821
      23501,Norfolk,VA,36.8959,-76.2085
      23508,Norfolk,VA,36.8859,-76.3004
      23220,Richmond,VA,37.5498,-77.4588
      24028,Roanoke,VA,37.2742,-79.9579
      24038,Roanoke,VA,37.2742,-79.9579
      23433,Suffolk,VA,36.909,-76.4929
      23456,Virginia Beach,VA,36.7341,-76.0359
      23463,Virginia Beach,VA,36.7957,-76.0126
      82071,Laramie,WY,41.3114,-105.5911
      82334,Sinclair,WY,41.7802,-107.1172
      82710,Aladdin,WY,44.7473,-104.1931
      82005,Fe Warren Afb,WY,41.1391,-104.8629
      83124,Opal,WY,41.7795,-110.276
      82210,Chugwater,WY,41.7487,-104.8179
      82922,Bondurant,WY,43.2238,-110.3353
      83025,Teton Village,WY,43.5924,-110.8314
      23412,Modest Town,VA,37.7904,-75.6035
      23483,Wattsville,VA,37.9437,-75.5023
      22924,Batesville,VA,38.0013,-78.7271
      24474,Selma,VA,37.8062,-79.8404
      24536,Coleman Falls,VA,37.4887,-79.3158
      24085,Eagle Rock,VA,37.6667,-79.8172
      24175,Troutville,VA,37.4019,-79.8786
      23821,Alberta,VA,36.8806,-77.9055
      24576,Naruna,VA,37.2458,-79.1335
      22446,Corbin,VA,38.1996,-77.3889
      22611,Berryville,VA,39.1532,-77.9688
      22663,White Post,VA,39.058,-78.1105
      23040,Cumberland,VA,37.5019,-78.2581
      22081,Merrifield,VA,38.8739,-77.2345
      22101,Mc Lean,VA,38.9326,-77.1706
      23103,Manakin Sabot,VA,37.638,-77.7077
      23069,Hanover,VA,37.7701,-77.3216
      23294,Henrico,VA,37.631,-77.546
      23431,Smithfield,VA,36.8989,-76.6877
      23127,Norge,VA,37.3688,-76.7705
      22513,Merry Point,VA,37.7338,-76.4825
      20159,Hamilton,VA,39.1339,-77.6621
      22711,Banco,VA,38.4512,-78.2817
      22922,Arrington,VA,37.6902,-78.9479
      22958,Nellysford,VA,37.902,-78.9004
      22969,Schuyler,VA,37.7976,-78.6925
      21053,Freeland,MD,39.694,-76.7223
      21093,Lutherville Timonium,MD,39.4332,-76.6546
      21153,Stevenson,MD,39.4104,-76.713
      21609,Bethlehem,MD,38.7406,-75.9587
      21791,Union Bridge,MD,39.5799,-77.1319
      21922,Elkton,MD,39.6068,-75.8333
      20602,Waldorf,MD,38.584,-76.8942
      21835,Linkwood,MD,38.5403,-75.963
      21018,Benson,MD,39.5093,-76.3851
      21029,Clarksville,MD,39.2125,-76.9515
      20905,Silver Spring,MD,39.1148,-77.0059
      20993,Silver Spring,MD,39.0336,-76.9861
      20717,Bowie,MD,38.8336,-76.8777
      20718,Bowie,MD,38.8336,-76.8777
      20731,Capitol Heights,MD,38.8851,-76.9158
      20750,Oxon Hill,MD,38.8034,-76.9897
      20785,Hyattsville,MD,38.9223,-76.8755
      20630,Drayden,MD,38.1719,-76.4731
      20653,Lexington Park,MD,38.2495,-76.4529
      21821,Deal Island,MD,38.1533,-75.9496
      21663,Saint Michaels,MD,38.783,-76.2215
      21665,Sherwood,MD,38.7374,-76.3278
      21676,Wittman,MD,38.7846,-76.3001
      21875,Delmar,MD,38.4445,-75.5583
      21864,Stockton,MD,38.0452,-75.4108
      21202,Baltimore,MD,39.2998,-76.6075
      21215,Baltimore,MD,39.3446,-76.6794
      23347,Eastville,VA,37.3526,-75.9458
      23955,Nottoway,VA,37.1161,-78.0578
      22942,Gordonsville,VA,38.1759,-78.1815
      22835,Luray,VA,38.6548,-78.4596
      23943,Hampden Sydney,VA,37.2381,-78.4618
      22195,Woodbridge,VA,38.6582,-77.2497
      24347,Hiwassee,VA,36.9756,-80.6695
      22827,Elkton,VA,38.4025,-78.6321
      22833,Lacey Spring,VA,38.5284,-78.8552
      24319,Chilhowie,VA,36.7719,-81.6651
      22403,Fredericksburg,VA,38.4173,-77.4608
      22471,Hartwood,VA,38.3993,-77.5814
      23883,Surry,VA,37.126,-76.7651
      24340,Glade Spring,VA,36.7779,-81.7676
      22443,Colonial Beach,VA,38.1792,-76.9959
      24360,Max Meadows,VA,36.9056,-80.9244
      23696,Seaford,VA,37.1885,-76.429
      22305,Alexandria,VA,38.8372,-77.064
      24523,Bedford,VA,37.3153,-79.5331
      24202,Bristol,VA,36.6609,-82.2126
      24209,Bristol,VA,36.5965,-82.1885
      24540,Danville,VA,36.6218,-79.4124
      22038,Fairfax,VA,38.8528,-77.302
      24515,Lynchburg,VA,37.4009,-79.1785
      24035,Roanoke,VA,37.2742,-79.9579
      22980,Waynesboro,VA,38.0774,-78.9035
      82410,Basin,WY,44.3788,-108.0438
      82428,Hyattville,WY,44.2507,-107.6053
      82615,Shirley Basin,WY,41.7169,-106.9992
      82243,Veteran,WY,41.9821,-104.3709
      82001,Cheyenne,WY,41.1437,-104.7962
      83111,Auburn,WY,42.8051,-110.9944
      82227,Manville,WY,42.73,-104.7024
      82213,Glendo,WY,42.5004,-105
      82845,Wyarno,WY,44.7534,-106.6949
      82936,Lonetree,WY,41.0552,-110.1603
      23389,Harborton,VA,37.6618,-75.8305
      22946,Keene,VA,37.856,-78.5699
      24463,Mint Spring,VA,38.1797,-79.1413
      23856,Freeman,VA,36.7893,-77.7208
      24603,Big Rock,VA,37.3532,-82.1968
      23936,Dillwyn,VA,37.4124,-78.4349
      22514,Milford,VA,38.0058,-77.3185
      24343,Hillsville,VA,36.7442,-80.7197
      23963,Red House,VA,37.1914,-78.8145
      20190,Reston,VA,38.9615,-77.3418
      22116,Merrifield,VA,38.8715,-77.2344
      22161,Springfield,VA,38.7893,-77.1872
      20187,Warrenton,VA,38.7153,-77.7417
      23055,Fork Union,VA,37.7715,-78.2355
      24088,Ferrum,VA,36.9168,-80.0349
      23184,Wicomico,VA,37.291,-76.5094
      24539,Crystal Hill,VA,36.8513,-78.9179
      23304,Battery Park,VA,36.9965,-76.5741
      23106,Manquin,VA,37.7184,-77.186
      20104,Dulles,VA,39.0853,-77.6452
      22738,Rochelle,VA,38.3215,-78.3042
      22920,Afton,VA,37.9626,-78.841
      20032,Washington,DC,38.8338,-76.9995
      20040,Washington,DC,38.8933,-77.0146
      20074,Washington,DC,38.8933,-77.0146
      20210,Washington,DC,38.8933,-77.0146
      20303,Washington,DC,38.8933,-77.0146
      20405,Washington,DC,38.8951,-77.0364
      20451,Washington,DC,38.8977,-77.0444
      20533,Washington,DC,38.9011,-77.0326
      20566,Washington,DC,38.8971,-77.0554
      20585,Washington,DC,38.8933,-77.0146
      21914,Charlestown,MD,39.5729,-75.9795
      21705,Frederick,MD,39.4143,-77.4105
      21001,Aberdeen,MD,39.5109,-76.1805
      21678,Worton,MD,39.2963,-76.1008
      20857,Rockville,MD,39.084,-77.1528
      20891,Kensington,MD,39.0257,-77.0764
      20892,Bethesda,MD,39.0024,-77.1034
      20901,Silver Spring,MD,39.0191,-77.0076
      20907,Silver Spring,MD,38.9907,-77.0261
      20912,Takoma Park,MD,38.9832,-77.0007
      20705,Beltsville,MD,39.0455,-76.9242
      20725,Laurel,MD,39.0993,-76.8483
      20742,College Park,MD,38.9896,-76.9457
      20744,Fort Washington,MD,38.7587,-76.9835
      20787,Hyattsville,MD,38.9871,-76.9824
      20628,Dameron,MD,38.1533,-76.3575
      20667,Park Hall,MD,38.2177,-76.4429
      20680,Ridge,MD,38.1169,-76.3711
      21836,Manokin,MD,38.1154,-75.7558
      21795,Williamsport,MD,39.593,-77.8087
      21217,Baltimore,MD,39.3064,-76.6393
      21288,Baltimore,MD,39.2847,-76.6205
      21122,Pasadena,MD,39.1206,-76.495
      21411,Annapolis,MD,38.9742,-76.5949
      21152,Sparks Glencoe,MD,39.5483,-76.6819
      21156,Upper Falls,MD,39.4372,-76.3966
      21163,Woodstock,MD,39.3498,-76.8456
      21901,North East,MD,39.6045,-75.9538
      21917,Colora,MD,39.6695,-76.0934
      20640,Indian Head,MD,38.6001,-77.1622
      20658,Marbury,MD,38.5633,-77.1596
      21659,Rhodesdale,MD,38.603,-75.7749
      21716,Brunswick,MD,39.3164,-77.623
      21798,Woodsboro,MD,39.5311,-77.2972
      21034,Darlington,MD,39.654,-76.2278
      21042,Ellicott City,MD,39.2726,-76.8614
      21645,Kennedyville,MD,39.2978,-75.9818
      20884,Gaithersburg,MD,39.144,-77.2076
      20886,Montgomery Village,MD,39.1757,-77.1873
      20918,Silver Spring,MD,38.9907,-77.0261
      20621,Chaptico,MD,38.351,-76.7833
      21647,Mcdaniel,MD,38.8192,-76.2806
      21849,Parsonsburg,MD,38.3914,-75.4737
      21851,Pocomoke City,MD,38.0714,-75.555
      21263,Baltimore,MD,39.2847,-76.6205
      21289,Baltimore,MD,39.2847,-76.6205
      20016,Washington,DC,38.9381,-77.086
      20029,Washington,DC,38.8933,-77.0146
      20039,Washington,DC,38.8933,-77.0146
      20213,Washington,DC,38.8933,-77.0146
      20220,Washington,DC,38.8933,-77.0146
      20224,Washington,DC,38.8933,-77.0146
      20238,Washington,DC,38.8933,-77.0146
      20251,Washington,DC,38.8933,-77.0146
      20318,Washington,DC,38.8933,-77.0146
      20406,Washington,DC,38.8933,-77.0146
      20542,Washington,DC,38.9408,-77.0283
      20572,Washington,DC,38.8933,-77.0146
      23423,Quinby,VA,37.5423,-75.7412
      22204,Arlington,VA,38.859,-77.0997
      24437,Fort Defiance,VA,38.2109,-78.9326
      24571,Lynch Station,VA,37.1528,-79.3297
      24328,Fancy Gap,VA,36.664,-80.6907
      23832,Chesterfield,VA,37.3923,-77.5668
      24127,New Castle,VA,37.4871,-80.1704
      23872,Mc Kenney,VA,36.9986,-77.7396
      22509,Loretto,VA,38.0789,-77.0485
      20172,Herndon,VA,38.9696,-77.3861
      22109,Mc Lean,VA,38.9335,-77.1797
      20128,Orlean,VA,38.7416,-77.9774
      23022,Bremo Bluff,VA,37.7453,-78.2672
      24146,Redwood,VA,37.0069,-79.9139
      22624,Clear Brook,VA,39.2655,-78.0988
      24086,Eggleston,VA,37.2743,-80.6343
      24598,Virgilina,VA,36.6062,-78.7605
      23015,Beaverdam,VA,37.9038,-77.6308
      23150,Sandston,VA,37.5157,-77.2758
      23229,Henrico,VA,37.4638,-77.398
      23238,Henrico,VA,37.52,-77.4369
      23397,Isle Of Wight,VA,36.8989,-76.6877
      20131,Philomont,VA,39.058,-77.7434
      20148,Ashburn,VA,39.0142,-77.5285
      20778,West River,MD,38.8252,-76.5391
      22456,Edwardsville,VA,37.9065,-76.3652
      22530,Ophelia,VA,37.9094,-76.2934
      22849,Shenandoah,VA,38.501,-78.609
      24531,Chatham,VA,36.831,-79.4297
      24563,Hurt,VA,37.0798,-79.3
      22026,Dumfries,VA,38.5669,-77.2921
      24415,Brownsburg,VA,37.9285,-79.3192
      24237,Dante,VA,37.0054,-82.2815
      22652,Fort Valley,VA,38.8407,-78.4276
      23867,Jarratt,VA,36.8191,-77.4832
      24613,Falls Mills,VA,37.271,-81.3182
      24637,Pounding Mill,VA,37.0596,-81.7301
      24327,Emory,VA,36.78,-81.8171
      22469,Hague,VA,38.0573,-76.6616
      22558,Stratford,VA,38.1484,-76.8413
      22302,Alexandria,VA,38.8276,-77.0896
      23669,Hampton,VA,37.0436,-76.3426
      20111,Manassas,VA,38.7707,-77.4494
      24273,Norton,VA,36.9378,-82.6249
      23173,Richmond,VA,37.5538,-77.4603
      24006,Roanoke,VA,37.2742,-79.9579
      24015,Roanoke,VA,37.2584,-79.9807
      24401,Staunton,VA,38.1574,-79.0651
      23434,Suffolk,VA,36.7304,-76.5931
      23438,Suffolk,VA,36.5913,-76.6871
      23462,Virginia Beach,VA,36.8392,-76.1522
      22601,Winchester,VA,39.1858,-78.1827
      82325,Encampment,WY,41.2054,-106.7803
      82327,Hanna,WY,41.8726,-106.5283
      82212,Fort Laramie,WY,42.2133,-104.5226
      82639,Kaycee,WY,43.7236,-106.5632
      82644,Mills,WY,42.8405,-106.3659
      82901,Rock Springs,WY,41.606,-109.23
      82933,Fort Bridger,WY,41.3166,-110.3843
      82401,Worland,WY,44.0138,-107.9563
      21409,Annapolis,MD,39.0416,-76.4377
      21030,Cockeysville,MD,39.4919,-76.6677
      21065,Hunt Valley,MD,39.4883,-76.6538
      21219,Sparrows Point,MD,39.2296,-76.4455
      20676,Port Republic,MD,38.4952,-76.5349
      21613,Cambridge,MD,38.5643,-76.0874
      21675,Wingate,MD,38.2899,-76.0863
      21869,Vienna,MD,38.4774,-75.8729
      21009,Abingdon,MD,39.4744,-76.2997
      20904,Silver Spring,MD,39.0668,-76.9969
      20914,Silver Spring,MD,38.9907,-77.0261
      20772,Upper Marlboro,MD,38.8377,-76.798
      21623,Church Hill,MD,39.146,-75.988
      21857,Rehobeth,MD,38.039,-75.663
      21852,Powellville,MD,38.3287,-75.3755
      23354,Franktown,VA,37.4588,-75.9007
      23429,Seaview,VA,37.2711,-75.9536
      23482,Wardtown,VA,37.5349,-75.8769
      22957,Montpelier Station,VA,38.227,-78.1768
      22972,Somerset,VA,38.1997,-78.2394
      24139,Pittsville,VA,36.9716,-79.4794
      22191,Woodbridge,VA,38.6356,-77.2683
      24132,Parrott,VA,37.2048,-80.6205
      22640,Huntly,VA,38.8129,-78.1165
      22644,Maurertown,VA,38.9441,-78.4658
      24354,Marion,VA,36.8273,-81.5349
      22463,Garrisonville,VA,38.4684,-77.4612
      24609,Cedar Bluff,VA,37.0876,-81.759
      22905,Charlottesville,VA,38.0401,-78.4851
      23630,Hampton,VA,37.0727,-76.3899
      24504,Lynchburg,VA,37.361,-79.0544
      23502,Norfolk,VA,36.8546,-76.2143
      23707,Portsmouth,VA,36.8362,-76.344
      24007,Roanoke,VA,37.2742,-79.9579
      24037,Roanoke,VA,37.2742,-79.9579
      23452,Virginia Beach,VA,36.8348,-76.0961
      82441,Shell,WY,44.6011,-107.7889
      82731,Weston,WY,44.7705,-105.3581
      82010,Cheyenne,WY,41.14,-104.8202
      83112,Bedford,WY,42.8701,-110.9401
      82643,Midwest,WY,43.4114,-106.28
      82435,Powell,WY,44.7561,-108.7773
      82844,Wolf,WY,44.7864,-107.2202
      82925,Cora,WY,43.1399,-109.9154
      82932,Farson,WY,42.0834,-109.4184
      82938,Mc Kinnon,WY,41.0409,-109.8745
      83012,Moose,WY,43.715,-110.742
      82931,Evanston,WY,41.2619,-110.92
      23404,Locustville,VA,37.6534,-75.6735
      23440,Tangier,VA,37.8245,-75.993
      23083,Jetersville,VA,37.3176,-78.104
      22225,Arlington,VA,38.8808,-77.1129
      22243,Arlington,VA,38.8605,-77.0516
      23147,Ruthville,VA,37.3796,-77.0348
      23838,Chesterfield,VA,37.3333,-77.6343
      22079,Lorton,VA,38.6929,-77.204
      22309,Alexandria,VA,38.7192,-77.1073
      20139,Casanova,VA,38.6543,-77.7025
      20184,Upperville,VA,38.9627,-77.8847
      23154,Schley,VA,37.3896,-76.4549
      23190,Woods Cross Roads,VA,37.482,-76.6362
      23067,Hadensville,VA,37.8252,-77.9899
      23153,Sandy Hook,VA,37.7526,-77.9125
      23185,Williamsburg,VA,37.2732,-76.7324
      23148,Saint Stephens Church,VA,37.8479,-77.055
      23161,Stevensville,VA,37.7146,-76.9352
      23009,Aylett,VA,37.8221,-77.1885
      24281,Rose Hill,VA,36.6583,-83.3486
      20134,Purcellville,VA,39.1522,-77.7026
      20141,Round Hill,VA,39.1164,-77.7802
      23950,La Crosse,VA,36.6613,-78.0754
      23970,South Hill,VA,36.7124,-78.1534
      21504,Cumberland,MD,39.5807,-78.6906
      21054,Gambrills,MD,39.0407,-76.6819
      20012,Washington,DC,38.9757,-77.0282
      20222,Washington,DC,38.8933,-77.0146
      20240,Washington,DC,38.8971,-77.0409
      20415,Washington,DC,38.8933,-77.0146
      20442,Washington,DC,38.896,-77.0177
      20508,Washington,DC,38.8933,-77.0146
      20520,Washington,DC,38.8932,-77.049
      20580,Washington,DC,38.8933,-77.0146
      20057,Washington,DC,38.8933,-77.0146
      20071,Washington,DC,38.8933,-77.0146
      20081,Washington,DC,38.8933,-77.0146
      20254,Washington,DC,38.8933,-77.0146
      20375,Washington,DC,38.8262,-77.0174
      20390,Washington,DC,38.8933,-77.0146
      20419,Washington,DC,38.8933,-77.0146
      20463,Washington,DC,38.8933,-77.0146
      20530,Washington,DC,38.8976,-77.027
      20540,Washington,DC,38.8874,-77.0047
      20559,Washington,DC,38.8874,-77.0047
      21062,Glen Burnie,MD,38.9742,-76.5949
      21082,Hydes,MD,39.474,-76.4695
      20610,Barstow,MD,38.5254,-76.6161
      21102,Manchester,MD,39.6747,-76.8941
      21915,Chesapeake City,MD,39.5133,-75.8406
      20637,Hughesville,MD,38.5207,-76.7817
      21669,Taylors Island,MD,38.4631,-76.2964
      21774,New Market,MD,39.4096,-77.2759
      21017,Belcamp,MD,39.4756,-76.242
      21028,Churchville,MD,39.5648,-76.249
      21046,Columbia,MD,39.1702,-76.8538
      20814,Bethesda,MD,39.0003,-77.1022
      20841,Boyds,MD,39.21,-77.3167
      20885,Gaithersburg,MD,39.1874,-77.2028
      20910,Silver Spring,MD,38.9982,-77.0338
      20799,Capitol Heights,MD,38.8336,-76.8777
      21654,Oxford,MD,38.6864,-76.1538
      21830,Hebron,MD,38.4026,-75.6963
      21865,Tyaskin,MD,38.2833,-75.8465
      21841,Newark,MD,38.2489,-75.2893
      23418,Onley,VA,37.6704,-75.6992
      23421,Parksley,VA,37.7744,-75.6386
      23426,Sanford,VA,37.9229,-75.6781
      23480,Wachapreague,VA,37.6044,-75.6925
      24572,Madison Heights,VA,37.4531,-79.1141
      22215,Arlington,VA,38.8808,-77.1129
      22226,Arlington,VA,38.8834,-77.1028
      24421,Churchville,VA,38.2346,-79.1791
      24431,Crimora,VA,38.1684,-78.8413
      24317,Cana,VA,36.5957,-80.6705
      22701,Culpeper,VA,38.5117,-77.9928
      22733,Rapidan,VA,38.3392,-78.0476
      22102,Mc Lean,VA,38.953,-77.2295
      22152,Springfield,VA,38.7757,-77.2337
      22153,Springfield,VA,38.7449,-77.237
      22185,Vienna,VA,38.8318,-77.2888
      22307,Alexandria,VA,38.7714,-77.0657
      24072,Check,VA,37.0446,-80.228
      24137,Penhook,VA,36.9201,-79.6645
      22622,Brucetown,VA,39.2543,-78.0664
      23107,Maryus,VA,37.2864,-76.4048
      24292,Whitetop,VA,36.6106,-81.5839
      24326,Elk Creek,VA,36.7306,-81.1915
      24534,Clover,VA,36.8637,-78.7862
      24597,Vernon Hill,VA,36.7853,-79.1095
      24458,Mc Dowell,VA,38.3266,-79.4988
      23181,West Point,VA,37.5556,-76.8237
      20142,Round Hill,VA,39.1307,-77.7747
      20165,Sterling,VA,39.0472,-77.3866
      23031,Christchurch,VA,37.5984,-76.4476
      23180,Water View,VA,37.7105,-76.6227
      21543,Midlothian,MD,39.6343,-78.95
      21560,Spring Gap,MD,39.5654,-78.7164
      23089,Lanexa,VA,37.4194,-76.9027
      23486,Willis Wharf,VA,37.5157,-75.8066
      22851,Stanley,VA,38.566,-78.5093
      22194,Woodbridge,VA,38.6582,-77.2497
      22820,Criders,VA,38.7497,-78.9974
      23874,Newsoms,VA,36.6148,-77.1069
      22649,Middletown,VA,39.0048,-78.2478
      24211,Abingdon,VA,36.6673,-81.9648
      23692,Yorktown,VA,37.1686,-76.4581
      22334,Alexandria,VA,38.8158,-77.0901
      22904,Charlottesville,VA,38.0401,-78.4851
      24333,Galax,VA,36.6565,-80.9117
      23670,Hampton,VA,37.0727,-76.3899
      23803,Petersburg,VA,37.2123,-77.4814
      23662,Poquoson,VA,37.1313,-76.3807
      82331,Saratoga,WY,41.498,-106.754
      82221,Lagrange,WY,41.6423,-104.1974
      82054,Carpenter,WY,41.0428,-104.2765
      83127,Thayne,WY,42.933,-111.0114
      82839,Ranchester,WY,44.9171,-107.174
      83414,Alta,WY,43.8886,-110.9492
      24464,Montebello,VA,37.8654,-79.0804
      23313,Capeville,VA,37.2019,-75.9524
      22542,Rhoadesville,VA,38.2863,-77.923
      24527,Blairs,VA,36.6799,-79.3731
      24566,Keeling,VA,36.7156,-79.2783
      20155,Gainesville,VA,38.8157,-77.6216
      24126,Newbern,VA,37.0692,-80.6891
      24070,Catawba,VA,37.3696,-80.1284
      24473,Rockbridge Baths,VA,37.8965,-79.3874
      22831,Hinton,VA,38.5689,-79.0885
      22660,Toms Brook,VA,38.9476,-78.4331
      22844,New Market,VA,38.6606,-78.6714
      23827,Boykins,VA,36.5951,-77.1975
      23839,Dendron,VA,37.0981,-76.8966
      24651,Tazewell,VA,37.1078,-81.51
      22524,Mount Holly,VA,38.1182,-76.6805
      24323,Crockett,VA,36.8768,-81.2089
      22332,Alexandria,VA,38.8031,-77.0727
      22902,Charlottesville,VA,38.0266,-78.4805
      23221,Richmond,VA,37.5583,-77.4845
      23261,Richmond,VA,37.5538,-77.4603
      23293,Richmond,VA,37.5242,-77.4932
      23451,Virginia Beach,VA,36.8585,-76.0019
      23186,Williamsburg,VA,37.3105,-76.7468
      82434,Otto,WY,44.4056,-108.3047
      82711,Alva,WY,44.6873,-104.4414
      82244,Yoder,WY,41.912,-104.3535
      82002,Cheyenne,WY,41.14,-104.8202
      82006,Cheyenne,WY,41.14,-104.8202
      82831,Arvada,WY,44.6899,-106.1092
      23396,Oak Hall,VA,37.9237,-75.5551
      22909,Charlottesville,VA,38.0245,-78.4482
      22212,Arlington,VA,38.8808,-77.1129
      24121,Moneta,VA,37.1784,-79.6521
      24239,Davenport,VA,37.1134,-82.1501
      24614,Grundy,VA,37.2967,-82.1061
      24646,Rowe,VA,37.1276,-82.0274
      24569,Long Island,VA,37.0644,-79.1219
      23934,Cullen,VA,37.1552,-78.6459
      22646,Millwood,VA,39.0696,-78.0378
      23822,Ammon,VA,37.0724,-77.6475
      22476,Hustle,VA,38.0329,-77.0633
      22560,Tappahannock,VA,37.9146,-76.9125
      22009,Burke,VA,38.8318,-77.2888
      20116,Marshall,VA,38.8537,-77.8601
      22656,Stephenson,VA,39.1973,-78.0941
      23050,Dutton,VA,37.5001,-76.454
      22973,Stanardsville,VA,38.3121,-78.482
      23060,Glen Allen,VA,37.6628,-77.534
      23289,Richmond,VA,37.5313,-77.4161
      24165,Spencer,VA,36.5968,-80.0373
      23156,Shacklefords,VA,37.5213,-76.7099
      24218,Ben Hur,VA,36.7435,-83.2234
      24221,Blackwater,VA,36.639,-82.9866
      24277,Pennington Gap,VA,36.7508,-83.0223
      20167,Sterling,VA,39.0062,-77.4286
      23117,Mineral,VA,37.9986,-77.8781
      22732,Radiant,VA,38.3146,-78.1899
      22948,Locust Dale,VA,38.3572,-78.1229
      23130,Onemo,VA,37.3985,-76.2941
      24149,Riner,VA,37.0322,-80.4353
      21505,Cumberland,MD,39.594,-78.8434
      21106,Mayo,MD,38.8876,-76.5119
      21240,Baltimore,MD,39.1753,-76.6732
      21052,Fort Howard,MD,39.207,-76.4456
      21071,Glyndon,MD,39.477,-76.815
      21092,Long Green,MD,39.4729,-76.523
      21757,Keymar,MD,39.5656,-77.2817
      21902,Perry Point,MD,39.553,-76.0725
      21758,Knoxville,MD,39.3479,-77.6513
      21778,Rocky Ridge,MD,39.6057,-77.3296
      21788,Thurmont,MD,39.6109,-77.3989
      21793,Walkersville,MD,39.4787,-77.3484
      21160,Whiteford,MD,39.7077,-76.316
      20849,Rockville,MD,39.084,-77.1528
      20853,Rockville,MD,39.0887,-77.095
      20871,Clarksburg,MD,39.2387,-77.2794
      20707,Laurel,MD,39.1077,-76.872
      20737,Riverdale,MD,38.9601,-76.9147
      21644,Ingleside,MD,39.1182,-75.8769
      20686,Saint Marys City,MD,38.1871,-76.4344
      21866,Tylerton,MD,37.9666,-76.0235
      21871,Westover,MD,38.101,-75.7406
      21625,Cordova,MD,38.8704,-76.0029
      21722,Clear Spring,MD,39.6658,-77.9064
      21224,Baltimore,MD,39.2876,-76.5568
      20007,Washington,DC,38.9144,-77.074
      20052,Washington,DC,38.9001,-77.0479
      20090,Washington,DC,38.8933,-77.0146
      20506,Washington,DC,38.8994,-77.0377
      20599,Washington,DC,38.8933,-77.0146
      """;
}