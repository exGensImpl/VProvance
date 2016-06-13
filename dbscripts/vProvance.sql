IF OBJECT_ID(N'vProvance', N'U') IS NOT NULL 
DROP DATABASE [vProvance]
GO

CREATE DATABASE vProvance
GO
 
USE vProvance;


/*===========Users============*/

create login vinodel with password = 'vinodel'
create user vinodel from login vinodel
GO
sp_addrolemember @rolename = 'vinodel',  
    @membername = 'exGens';  	
GO

create login exGens with password = 'exGens'
create user exGens from login exGens
GO
sp_addrolemember @rolename = 'db_owner',  
    @membername = 'exGens';  	
GO

/*===========Tables============*/

create table places
(
	ID tinyint not null identity(0,1),
	description nvarchar(256) not null,
	capacity int,
	primary key (ID)
);

create table resourses
(
	ID smallint not null identity(0,1),
	description nvarchar(256) not null,
	groupID smallint,
	measure nvarchar(15),
	cost money,
	
	primary key (ID),
	foreign key (groupID) references resourses
);
 
create table batches
(
	ID int not null identity(0,1),
	description varchar(255),
	count real not null check (count > 0), 
	cost money check (cost > 0),
	resourceID smallint not null,
	placeID tinyint not null,
	productionDate datetime,
	lastMovingDate datetime,

	primary key (ID),
	foreign key (resourceID) references resourses,
	foreign key (placeID) references places
);

create table transactionsType
(
	ID smallint not null identity(0,1),
	description nvarchar(256) not null,
	primary key (ID)
);

create table transactions
(
	ID int not null identity(0,1),
	batchID int not null,
	typeID smallint not null,
	date datetime not null,
	subject smallint not null,
	object smallint,

	primary key (ID),
	foreign key (batchID) references batches,
    foreign key (typeID) references transactionsType
);
GO

create table userTypes
(
	ID smallint not null identity(0,1),
	description nvarchar(256) not null,
	primary key (ID)
);

create table users
(
	ID int not null identity(0,1),
	name nvarchar(256) not null,
	roleID smallint not null,
	userID varbinary(85) not null,

	primary key (ID),
	foreign key (roleID) references userTypes
);
GO


/*===========Tables content============*/

insert into transactionsType(description)
values ('Приём товара'), ('Списание товара'), ('Продажа товара'), ('Перемещение товара');
GO

insert into places(description)
values ('Склад'), ('Магазин'), ('Поле'), ('Производственный цех');
GO

insert into userTypes(description)
values ('Винодел'), ('Менеджер'), ('Продавец'), ('Завхоз'), ('Администратор');
GO

insert into resourses(description, groupID, measure, cost)
values ('Виноград', null, 'кг', null), ('Мерло', 0, 'кг', 1.2), ('Шардонне', 0, 'кг', 2.4), ('Рислинг', 0, 'кг', 1.4);
GO

insert into users(name, roleID, userID)
values ('Пётр Винодел',
		(select top(1) id from userTypes where description like 'Винодел'), 
		(select top(1) sid from sys.syslogins where name like 'vinodel')),
		
		('Кирилл',
		(select top(1) id from userTypes where description like 'Администратор'), 
		(select top(1) sid from sys.syslogins where name like 'exGens'));
GO


/*===========Procedures============*/

IF OBJECT_ID(N'dbo.AddBatch', N'U') IS NOT NULL 
drop procedure dbo.AddBatch
GO
create procedure dbo.AddBatch
	@ResourceName nvarchar(256),
	@Count real,
	@Cost money,
	@PlaceName nvarchar(256),
	@ProductionDate datetime,
	@Description nvarchar(256)
	AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @ResourceID smallint;
	DECLARE @PlaceID tinyint;
	DECLARE @TransactionTypeID smallint;	

	set @ResourceID = (select top(1) ID from resourses where description like @ResourceName);
	set @PlaceID = (select top(1) ID from places where description like @PlaceName);
	set @TransactionTypeID = (select top(1) ID from transactionsType where description like 'Приём товара');
	if (@Cost is null) set @Cost = @Count * (select top(1) cost from resourses where description like @ResourceName);

	if ((@ResourceID is null) or (@PlaceID is null) or (@TransactionTypeID is null)) return 1;

	begin transaction	
	insert into batches(description, count, cost, resourceID, placeID, productionDate, lastMovingDate)
	values (@Description, @Count, @Cost, @ResourceID, @PlaceID, @ProductionDate, CURRENT_TIMESTAMP);

	if (@@ROWCOUNT = 0) 
	BEGIN
		rollback transaction
		return 2;
	END

	insert into transactions(batchID, typeID, date, subject, object)
	values (@@IDENTITY, @TransactionTypeID, CURRENT_TIMESTAMP, USER_ID(CURRENT_USER), null)

	commit transaction
	return 0;
END
GO

IF OBJECT_ID(N'dbo.SetCurrentUserInfo', N'U') IS NOT NULL 
drop procedure dbo.SetCurrentUserInfo
GO
create procedure dbo.SetCurrentUserInfo
	@Name nvarchar(256),
	@Role nvarchar(256) = null
	AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @RoleID smallint;

	if(@RoleID is not null)
		set @RoleID = (select top(1) ID from userTypes where description like @Role);
	else 
		set @RoleID = (
			select top(1) ID 
			from userTypes 
			where description = 
				(select top(1) role from dbo.CurrentUserInfo));
	
	if (@RoleID is null) return 1;

	begin transaction	
	update users
	set name = @Name, roleID = @RoleID
	where name = (select top(1) name from dbo.CurrentUserInfo);

	if (@@ROWCOUNT = 0) 
	BEGIN
		rollback transaction
		return 2;
	END

	commit transaction
	return 0;
END
GO

/*===========Views============*/

IF OBJECT_ID(N'[dbo].[UsefullBatches]', N'U') IS NOT NULL 
DROP VIEW [dbo].[UsefullBatches]
GO
CREATE VIEW [dbo].[UsefullBatches]
AS
SELECT      dbo.resourses.description AS [resource type], 
			dbo.batches.count, dbo.resourses.measure, 
			dbo.batches.description, dbo.batches.cost, 
			dbo.batches.productionDate, 
			dbo.places.description AS [place name]

FROM            dbo.batches INNER JOIN
                dbo.resourses ON dbo.batches.resourceID = dbo.resourses.ID INNER JOIN
                dbo.places ON dbo.batches.placeID = dbo.places.ID
GO

IF OBJECT_ID(N'[dbo].[UsefullTransactions]', N'U') IS NOT NULL 
DROP VIEW [dbo].[UsefullTransactions]
GO
CREATE VIEW [dbo].[UsefullTransactions]
AS
SELECT      dbo.transactions.date, 
			dbo.transactionsType.description AS action, 
			dbo.resourses.description AS resource, 
			dbo.batches.count, dbo.resourses.measure, 
			sys.sysusers.name AS subject, 
			sysusers_1.name AS object

FROM            dbo.transactions INNER JOIN
				dbo.transactionsType ON dbo.transactions.typeID = dbo.transactionsType.ID INNER JOIN
				dbo.batches ON dbo.transactions.batchID = dbo.batches.ID INNER JOIN
				dbo.resourses ON dbo.batches.resourceID = dbo.resourses.ID INNER JOIN
				sys.sysusers ON dbo.transactions.subject = sys.sysusers.uid LEFT JOIN
 				sys.sysusers AS sysusers_1 ON dbo.transactions.object = sysusers_1.uid
GO

IF OBJECT_ID(N'[dbo].[CurrentUserInfo]', N'U') IS NOT NULL 
DROP VIEW [dbo].[CurrentUserInfo]
GO
CREATE VIEW [dbo].[CurrentUserInfo]
AS
SELECT	sys.syslogins.name as username, 
		users.name, 
		userTypes.description as role
FROM    users INNER JOIN
		userTypes ON users.roleID = userTypes.ID
		INNER JOIN sys.syslogins ON users.userID = sys.syslogins.sid
WHERE	sys.syslogins.name = CURRENT_USER
GO

EXEC	[dbo].[AddBatch]
		@ResourceName = 'Мерло',
		@Count = 2,
		@Cost = 23,
		@PlaceName = 'Склад',
		@ProductionDate = '2015.20.12',
		@Description = 'Вот и партия мерла. Я от счастья умерла'
GO