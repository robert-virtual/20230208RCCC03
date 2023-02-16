create database BD20230209RCC

use BD20230209RCC

CREATE TABLE [role] (
    [id] int PRIMARY KEY IDENTITY(1, 1),
    [name] varchar(20)
    )










CREATE TABLE [users] (
    [id] bigint PRIMARY KEY IDENTITY(1, 1),
    [role] int,
    [password] nvarchar(420),
    [email] nvarchar(320) UNIQUE NOT NULL,
    [otp] nvarchar(8),
    [otp_expires_in] datetime,
    [failed_logins] int DEFAULT (0),
    [status] bit DEFAULT (1),
    [last_login] datetime,
    [created_at] datetime DEFAULT (getdate()),
    [updated_at] datetime,
    [customer_id] bigint
    )


CREATE TABLE [payrolle] (
    [id] bigint PRIMARY KEY IDENTITY(1, 1),
    [name] nvarchar(50),
    [amount] nvarchar,
    [user_id] bigint,
    [status] bit DEFAULT (1)
    )


CREATE TABLE [account_type] (
    [id] int PRIMARY KEY IDENTITY(1, 1),
    [name] nvarchar(20)
    )





CREATE TABLE [customer] (
    [id] bigint PRIMARY KEY IDENTITY(1, 1),
    [phone] nvarchar(14) UNIQUE NOT NULL,
    [name] nvarchar(100),
    [lastname] nvarchar(100),
    [birthdate] date,
    [email] nvarchar(320) UNIQUE NOT NULL,
    [address_1] nvarchar(255),
    [address_2] nvarchar(255),
    [created_at] datetime DEFAULT (getdate()),
    [updated_at] datetime
    )





CREATE TABLE [payroll_customer] (
    [customer_id] bigint,
[payroll_id] bigint
)


CREATE TABLE [account] (
    [id] bigint PRIMARY KEY IDENTITY(1000000000000000, 1),
    [account_type] int,
    [company] bit DEFAULT (0),
    [available_balance] nvarchar DEFAULT (0),
    [held_balance] nvarchar DEFAULT (0),
    [customer_id] bigint,
    [created_at] datetime DEFAULT (getdate())
    )


CREATE TABLE [provider] (
    [id] bigint PRIMARY KEY IDENTITY(10000000, 1),
    [name] nvarchar(50),
    [customer_id] bigint
    )


CREATE TABLE [service] (
    [id] bigint PRIMARY KEY IDENTITY(1, 1),
    [name] nvarchar,
    [price] money,
    [status] bit DEFAULT (1)
    )


CREATE TABLE [service_provider] (
    [provider_id] bigint,
[service_id] bigint
)


CREATE TABLE [transaction_type] (
    [id] int PRIMARY KEY IDENTITY(1, 1),
    [name] nvachar(20),
    [description] nvachar(50),
    [status] bit DEFAULT (1)
    )






CREATE TABLE [transaction_status] (
    [id] int PRIMARY KEY IDENTITY(1, 1),
    [name] nvarchar(50)
    )


    5



CREATE TABLE [transaction] (
    [id] bigint PRIMARY KEY IDENTITY(1, 1),
    [user_id] bigint,
    [source_account] bigint,
    [transacction_type] int,
    [currency] nvarchar(5),
    [date] datetime DEFAULT (getdate()),
    [status] int
    )


CREATE TABLE [transaction_target_account] (
    [transacction_id] bigint,
    [target_account] bigint,
[amount] nvarchar
)


CREATE TABLE [audit_log] (
    [id] bigint PRIMARY KEY IDENTITY(1, 1),
    [action] nvarchar,
    [user_id] bigint,
    [data] nvarchar NOT NULL,
    [date] datetime DEFAULT (getdate())
    )


ALTER TABLE [users] ADD FOREIGN KEY ([role]) REFERENCES [role] ([id])


ALTER TABLE [users] ADD FOREIGN KEY ([customer_id]) REFERENCES [customer] ([id])


ALTER TABLE [payrolle] ADD FOREIGN KEY ([user_id]) REFERENCES [users] ([id])


ALTER TABLE [payroll_customer] ADD FOREIGN KEY ([customer_id]) REFERENCES [customer] ([id])


ALTER TABLE [payroll_customer] ADD FOREIGN KEY ([payroll_id]) REFERENCES [payrolle] ([id])


ALTER TABLE [account] ADD FOREIGN KEY ([account_type]) REFERENCES [account_type] ([id])


ALTER TABLE [account] ADD FOREIGN KEY ([customer_id]) REFERENCES [customer] ([id])


ALTER TABLE [provider] ADD FOREIGN KEY ([customer_id]) REFERENCES [customer] ([id])


ALTER TABLE [service_provider] ADD FOREIGN KEY ([provider_id]) REFERENCES [provider] ([id])


ALTER TABLE [service_provider] ADD FOREIGN KEY ([service_id]) REFERENCES [service] ([id])


ALTER TABLE [transaction] ADD FOREIGN KEY ([user_id]) REFERENCES [users] ([id])


ALTER TABLE [transaction] ADD FOREIGN KEY ([source_account]) REFERENCES [account] ([id])


ALTER TABLE [transaction] ADD FOREIGN KEY ([transacction_type]) REFERENCES [transacction_type] ([id])


ALTER TABLE [transaction] ADD FOREIGN KEY ([status]) REFERENCES [transaction_status] ([id])


ALTER TABLE [transaction_target_account] ADD FOREIGN KEY ([transacction_id]) REFERENCES [transaction] ([id])


ALTER TABLE [transaction_target_account] ADD FOREIGN KEY ([target_account]) REFERENCES [account] ([id])


ALTER TABLE [audit_log] ADD FOREIGN KEY ([user_id]) REFERENCES [users] ([id])





    insert into role values('operator','authorizer')



    insert into users(
    role,
    password,
    email,
    failed_logins,
    customer_id
    ) values(
    2,
    '$2a$10$C5CdRLTM/avCMpDxbAunReo9us9eL0LYylj.s2OybMFJylgtAmKAO',
    'robertocastillo945@gmail.com',
    0,
    1
    )

    insert into account_type values('saving'),('checking')



    insert into customer(
    name,
    lastname,
    birthdate,
    phone,
    email
    ) values(
    'Roberto',
    'Castillo',
    '2000-08-31',
    '88137603',
    'robertocastillo945@gmail.com'
    )


    insert into transaction_type(name,description)
    values ('CCA','Credito a cuenta ahorro'),
    ('CCH','Credito a cuenta de cheques'),
    ('ACH','Transferencia a otros bancos'),
    ('PPA','Pago de planillas'),
    ('PPR','Pago de proveedores')





    insert into transaction_status values('pending'),('authorized')
