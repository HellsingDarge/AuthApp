INSERT INTO UsersCredentials VALUES (
    'sasha',
    'V9Me2nx',
    'bc4725cd5915a9cda45d2835bdd8e444be15c7c9aabdd0dc8693d7a7d2500dc3'
);
INSERT INTO UsersCredentials VALUES (
    'admin',
    '6xInN0l',
    'e0feb157dadff817c0f11b48d0441e56b475a27289117c6cb1ca7fd0b108b13c'
);
INSERT INTO UsersCredentials VALUES (
    'q',
    '4bxdOU7',
    '2002c9e01093b6d8b7d3699d6b7bd1a5fb8200340b1275f52ae5794d59854849'
);
INSERT INTO UsersCredentials VALUES (
    'abcdefghij',
    'TM36tOy',
    'd880929e469c4a2c19352f76460853be52ee581f7fcdd3097f86f670f690e910'
);

INSERT INTO Resources(resource, role, user) VALUES('A', 'READ', 'sasha');
INSERT INTO Resources(resource, role, user) VALUES('A.AA', 'WRITE', 'sasha');
INSERT INTO Resources(resource, role, user) VALUES('A.AA.AAA', 'EXECUTE', 'sasha');
INSERT INTO Resources(resource, role, user) VALUES('B', 'EXECUTE', 'admin');
INSERT INTO Resources(resource, role, user) VALUES('A.B', 'WRITE', 'admin');
INSERT INTO Resources(resource, role, user) VALUES('A.B', 'WRITE', 'sasha');
INSERT INTO Resources(resource, role, user) VALUES('A.B.C', 'READ', 'admin');
INSERT INTO Resources(resource, role, user) VALUES('A.B.C', 'WRITE', 'q');
INSERT INTO Resources(resource, role, user) VALUES('A.B', 'EXECUTE', 'q');
INSERT INTO Resources(resource, role, user) VALUES('B', 'READ', 'q');
INSERT INTO Resources(resource, role, user) VALUES('A.AA.AAA', 'READ', 'q');
INSERT INTO Resources(resource, role, user) VALUES('A', 'EXECUTE', 'q');
INSERT INTO Resources(resource, role, user) VALUES('A', 'WRITE', 'admin');
INSERT INTO Resources(resource, role, user) VALUES('A.AA', 'EXECUTE', 'admin');
INSERT INTO Resources(resource, role, user) VALUES('B', 'WRITE', 'sasha');
INSERT INTO Resources(resource, role, user) VALUES('A.B', 'EXECUTE', 'sasha');
INSERT INTO Resources(resource, role, user) VALUES('A.B.C', 'EXECUTE', 'sasha');