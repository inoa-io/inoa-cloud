INSERT INTO tenant (id,uuid,name,created,updated) VALUES
	(1,'00000000-0000-0000-0000-000000000000','kokuwa',NOW(),NOW()),
	(2,UUID(),'customer-2',NOW(),NOW()),
	(3,UUID(),'customer-3',NOW(),NOW());
INSERT INTO gateway (id,tenant_id,uuid,serial,created,updated) VALUES
	(1,1,'00000000-0000-0000-0000-000000000000','GW-00001',NOW(),NOW()),
	(2,1,'00000000-0000-0000-0000-000000000001','GW-00002',NOW(),NOW()),
	(3,1,'00000000-0000-0000-0000-000000000002','GW-00003',NOW(),NOW()),
	(4,1,UUID(),'GW-00004',NOW(),NOW()),
	(5,1,UUID(),'GW-00005',NOW(),NOW()),
	(6,2,UUID(),'GW-00001',NOW(),NOW());
INSERT INTO gateway_property (id,gateway_id,key,value,created,updated) VALUES
	(1,1,'aaa','a',NOW(),NOW()),
	(2,1,'bbb','b',NOW(),NOW());
