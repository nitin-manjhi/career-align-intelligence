INSERT INTO states (id, name) VALUES
                                  (1,'Andhra Pradesh'),
                                  (2,'Arunachal Pradesh'),
                                  (3,'Assam'),
                                  (4,'Bihar'),
                                  (5,'Chhattisgarh'),
                                  (6,'Goa'),
                                  (7,'Gujarat'),
                                  (8,'Haryana'),
                                  (9,'Himachal Pradesh'),
                                  (10,'Jharkhand'),
                                  (11,'Karnataka'),
                                  (12,'Kerala'),
                                  (13,'Madhya Pradesh'),
                                  (14,'Maharashtra'),
                                  (15,'Manipur'),
                                  (16,'Meghalaya'),
                                  (17,'Mizoram'),
                                  (18,'Nagaland'),
                                  (19,'Odisha'),
                                  (20,'Punjab'),
                                  (21,'Rajasthan'),
                                  (22,'Sikkim'),
                                  (23,'Tamil Nadu'),
                                  (24,'Telangana'),
                                  (25,'Tripura'),
                                  (26,'Uttar Pradesh'),
                                  (27,'Uttarakhand'),
                                  (28,'West Bengal'),
                                  (29,'Andaman and Nicobar Islands'),
                                  (30,'Chandigarh'),
                                  (31,'Dadra and Nagar Haveli and Daman and Diu'),
                                  (32,'Delhi'),
                                  (33,'Jammu and Kashmir'),
                                  (34,'Ladakh'),
                                  (35,'Lakshadweep'),
                                  (36,'Puducherry')
ON CONFLICT (id) DO NOTHING;

INSERT INTO cities (id, name, state_id) VALUES

-- Andhra Pradesh
(1001,'Visakhapatnam',1),(1002,'Vijayawada',1),(1003,'Guntur',1),(1004,'Nellore',1),(1005,'Kurnool',1),
(1006,'Rajahmundry',1),(1007,'Tirupati',1),(1008,'Kadapa',1),(1009,'Anantapur',1),(1010,'Eluru',1),

-- Arunachal Pradesh
(1101,'Itanagar',2),(1102,'Tawang',2),(1103,'Pasighat',2),(1104,'Ziro',2),(1105,'Bomdila',2),
(1106,'Tezu',2),(1107,'Roing',2),(1108,'Along',2),(1109,'Daporijo',2),(1110,'Namsai',2),

-- Assam
(1201,'Guwahati',3),(1202,'Silchar',3),(1203,'Dibrugarh',3),(1204,'Jorhat',3),(1205,'Nagaon',3),
(1206,'Tinsukia',3),(1207,'Tezpur',3),(1208,'Karimganj',3),(1209,'Sivasagar',3),(1210,'Goalpara',3),

-- Bihar
(1301,'Patna',4),(1302,'Gaya',4),(1303,'Bhagalpur',4),(1304,'Muzaffarpur',4),(1305,'Darbhanga',4),
(1306,'Purnia',4),(1307,'Ara',4),(1308,'Begusarai',4),(1309,'Katihar',4),(1310,'Munger',4),

-- Chhattisgarh
(1401,'Raipur',5),(1402,'Bhilai',5),(1403,'Bilaspur',5),(1404,'Korba',5),(1405,'Durg',5),
(1406,'Rajnandgaon',5),(1407,'Jagdalpur',5),(1408,'Ambikapur',5),(1409,'Raigarh',5),(1410,'Dhamtari',5),

-- Goa
(1501,'Panaji',6),(1502,'Margao',6),(1503,'Vasco da Gama',6),(1504,'Mapusa',6),(1505,'Ponda',6),
(1506,'Bicholim',6),(1507,'Curchorem',6),(1508,'Sanquelim',6),(1509,'Valpoi',6),(1510,'Quepem',6),

-- Gujarat
(1601,'Ahmedabad',7),(1602,'Surat',7),(1603,'Vadodara',7),(1604,'Rajkot',7),(1605,'Bhavnagar',7),
(1606,'Jamnagar',7),(1607,'Junagadh',7),(1608,'Gandhinagar',7),(1609,'Anand',7),(1610,'Morbi',7),

-- Haryana
(1701,'Faridabad',8),(1702,'Gurugram',8),(1703,'Panipat',8),(1704,'Ambala',8),(1705,'Yamunanagar',8),
(1706,'Rohtak',8),(1707,'Hisar',8),(1708,'Karnal',8),(1709,'Sonipat',8),(1710,'Panchkula',8),

-- Himachal Pradesh
(1801,'Shimla',9),(1802,'Mandi',9),(1803,'Solan',9),(1804,'Dharamshala',9),(1805,'Kullu',9),
(1806,'Chamba',9),(1807,'Nahan',9),(1808,'Bilaspur',9),(1809,'Hamirpur',9),(1810,'Una',9),

-- Jharkhand
(1901,'Ranchi',10),(1902,'Jamshedpur',10),(1903,'Dhanbad',10),(1904,'Bokaro',10),(1905,'Hazaribagh',10),
(1906,'Deoghar',10),(1907,'Giridih',10),(1908,'Ramgarh',10),(1909,'Medininagar',10),(1910,'Chaibasa',10),

-- Karnataka
(2001,'Bengaluru',11),(2002,'Mysuru',11),(2003,'Hubballi',11),(2004,'Mangaluru',11),(2005,'Belagavi',11),
(2006,'Ballari',11),(2007,'Davangere',11),(2008,'Shivamogga',11),(2009,'Tumakuru',11),(2010,'Udupi',11),

-- Kerala
(2101,'Thiruvananthapuram',12),(2102,'Kochi',12),(2103,'Kozhikode',12),(2104,'Thrissur',12),(2105,'Kannur',12),
(2106,'Kollam',12),(2107,'Alappuzha',12),(2108,'Palakkad',12),(2109,'Kottayam',12),(2110,'Malappuram',12),

-- Madhya Pradesh
(2201,'Bhopal',13),(2202,'Indore',13),(2203,'Gwalior',13),(2204,'Jabalpur',13),(2205,'Ujjain',13),
(2206,'Sagar',13),(2207,'Dewas',13),(2208,'Satna',13),(2209,'Ratlam',13),(2210,'Rewa',13),

-- Maharashtra
(2301,'Mumbai',14),(2302,'Pune',14),(2303,'Nagpur',14),(2304,'Thane',14),(2305,'Nashik',14),
(2306,'Aurangabad',14),(2307,'Solapur',14),(2308,'Amravati',14),(2309,'Kolhapur',14),(2310,'Nanded',14),

-- Manipur
(2401,'Imphal',15),(2402,'Thoubal',15),(2403,'Bishnupur',15),(2404,'Churachandpur',15),(2405,'Kakching',15),
(2406,'Ukhrul',15),(2407,'Senapati',15),(2408,'Tamenglong',15),(2409,'Jiribam',15),(2410,'Moreh',15),

-- Meghalaya
(2501,'Shillong',16),(2502,'Tura',16),(2503,'Nongpoh',16),(2504,'Jowai',16),(2505,'Baghmara',16),
(2506,'Williamnagar',16),(2507,'Resubelpara',16),(2508,'Mawkyrwat',16),(2509,'Khliehriat',16),(2510,'Ampati',16),

-- Mizoram
(2601,'Aizawl',17),(2602,'Lunglei',17),(2603,'Saiha',17),(2604,'Champhai',17),(2605,'Kolasib',17),
(2606,'Serchhip',17),(2607,'Lawngtlai',17),(2608,'Saitual',17),(2609,'Khawzawl',17),(2610,'Hnahthial',17),

-- Nagaland
(2701,'Kohima',18),(2702,'Dimapur',18),(2703,'Mokokchung',18),(2704,'Tuensang',18),(2705,'Wokha',18),
(2706,'Zunheboto',18),(2707,'Phek',18),(2708,'Kiphire',18),(2709,'Longleng',18),(2710,'Peren',18),

-- Odisha
(2801,'Bhubaneswar',19),(2802,'Cuttack',19),(2803,'Rourkela',19),(2804,'Berhampur',19),(2805,'Sambalpur',19),
(2806,'Puri',19),(2807,'Balasore',19),(2808,'Bhadrak',19),(2809,'Baripada',19),(2810,'Jharsuguda',19),

-- Punjab
(2901,'Ludhiana',20),(2902,'Amritsar',20),(2903,'Jalandhar',20),(2904,'Patiala',20),(2905,'Bathinda',20),
(2906,'Mohali',20),(2907,'Hoshiarpur',20),(2908,'Pathankot',20),(2909,'Moga',20),(2910,'Abohar',20),

-- Rajasthan
(3001,'Jaipur',21),(3002,'Jodhpur',21),(3003,'Udaipur',21),(3004,'Kota',21),(3005,'Bikaner',21),
(3006,'Ajmer',21),(3007,'Alwar',21),(3008,'Bharatpur',21),(3009,'Sikar',21),(3010,'Pali',21),

-- Sikkim
(3101,'Gangtok',22),(3102,'Namchi',22),(3103,'Gyalshing',22),(3104,'Mangan',22),(3105,'Rangpo',22),
(3106,'Singtam',22),(3107,'Jorethang',22),(3108,'Ravangla',22),(3109,'Soreng',22),(3110,'Yuksom',22),

-- Tamil Nadu
(3201,'Chennai',23),(3202,'Coimbatore',23),(3203,'Madurai',23),(3204,'Salem',23),(3205,'Tiruchirappalli',23),
(3206,'Tirunelveli',23),(3207,'Erode',23),(3208,'Vellore',23),(3209,'Thoothukudi',23),(3210,'Dindigul',23),

-- Telangana
(3301,'Hyderabad',24),(3302,'Warangal',24),(3303,'Nizamabad',24),(3304,'Karimnagar',24),(3305,'Khammam',24),
(3306,'Ramagundam',24),(3307,'Mahbubnagar',24),(3308,'Adilabad',24),(3309,'Suryapet',24),(3310,'Miryalaguda',24),

-- Tripura
(3401,'Agartala',25),(3402,'Udaipur',25),(3403,'Dharmanagar',25),(3404,'Kailashahar',25),(3405,'Belonia',25),
(3406,'Khowai',25),(3407,'Ambassa',25),(3408,'Sabroom',25),(3409,'Sonamura',25),(3410,'Teliamura',25),

-- Uttar Pradesh
(3501,'Lucknow',26),(3502,'Kanpur',26),(3503,'Ghaziabad',26),(3504,'Agra',26),(3505,'Varanasi',26),
(3506,'Meerut',26),(3507,'Prayagraj',26),(3508,'Bareilly',26),(3509,'Aligarh',26),(3510,'Moradabad',26),

-- Uttarakhand
(3601,'Dehradun',27),(3602,'Haridwar',27),(3603,'Roorkee',27),(3604,'Haldwani',27),(3605,'Rudrapur',27),
(3606,'Kashipur',27),(3607,'Rishikesh',27),(3608,'Kotdwar',27),(3609,'Pithoragarh',27),(3610,'Almora',27),

-- West Bengal
(3701,'Kolkata',28),(3702,'Howrah',28),(3703,'Durgapur',28),(3704,'Asansol',28),(3705,'Siliguri',28),
(3706,'Kharagpur',28),(3707,'Haldia',28),(3708,'Bardhaman',28),(3709,'Malda',28),(3710,'Jalpaiguri',28),

-- union Territories
(4001,'Port Blair',29),
(4002,'Diglipur',29),
(4003,'Mayabunder',29),
(4004,'Rangat',29),
(4005,'Havelock Island',29),
(4006,'Neil Island',29),
(4007,'Car Nicobar',29),
(4008,'Bamboo Flat',29),
(4009,'Garacharma',29),
(4010,'Wimberlygunj',29),

-- Chandigarh (30)
(4101,'Chandigarh',30),
(4102,'Manimajra',30),
(4103,'Burail',30),
(4104,'Daria',30),
(4105,'Kaimbwala',30),
(4106,'Khuda Lahora',30),
(4107,'Khuda Jassu',30),
(4108,'Raipur Kalan',30),
(4109,'Raipur Khurd',30),
(4110,'Behlana',30),

-- Dadra and Nagar Haveli and Daman and Diu (31)
(4201,'Daman',31),
(4202,'Diu',31),
(4203,'Silvassa',31),
(4204,'Naroli',31),
(4205,'Vapi',31),
(4206,'Amli',31),
(4207,'Dadra',31),
(4208,'Kachigam',31),
(4209,'Samarvarni',31),
(4210,'Masat',31),

-- Delhi (32)
(4301,'New Delhi',32),
(4302,'Dwarka',32),
(4303,'Rohini',32),
(4304,'Saket',32),
(4305,'Karol Bagh',32),
(4306,'Lajpat Nagar',32),
(4307,'Pitampura',32),
(4308,'Janakpuri',32),
(4309,'Vasant Kunj',32),
(4310,'Shahdara',32),

-- Jammu and Kashmir (33)
(4401,'Srinagar',33),
(4402,'Jammu',33),
(4403,'Anantnag',33),
(4404,'Baramulla',33),
(4405,'Kathua',33),
(4406,'Kupwara',33),
(4407,'Pulwama',33),
(4408,'Udhampur',33),
(4409,'Rajouri',33),
(4410,'Poonch',33),

-- Ladakh (34)
(4501,'Leh',34),
(4502,'Kargil',34),
(4503,'Diskit',34),
(4504,'Nyoma',34),
(4505,'Zanskar',34),
(4506,'Dras',34),
(4507,'Tangtse',34),
(4508,'Khaltse',34),
(4509,'Turtuk',34),
(4510,'Padum',34),

-- Lakshadweep (35)
(4601,'Kavaratti',35),
(4602,'Agatti',35),
(4603,'Minicoy',35),
(4604,'Amini',35),
(4605,'Andrott',35),
(4606,'Kalpeni',35),
(4607,'Kadmat',35),
(4608,'Kiltan',35),
(4609,'Chetlat',35),
(4610,'Bitra',35),

-- Puducherry (36)
(4701,'Puducherry',36),
(4702,'Karaikal',36),
(4703,'Mahe',36),
(4704,'Yanam',36),
(4705,'Oulgaret',36),
(4706,'Villianur',36),
(4707,'Bahour',36),
(4708,'Ariyankuppam',36),
(4709,'Mudaliarpet',36),
(4710,'Thirunallar',36)
ON CONFLICT (id) DO NOTHING;
