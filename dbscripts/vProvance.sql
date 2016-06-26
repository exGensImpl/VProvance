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
sp_addrolemember @rolename = 'db_owner',  
    @membername = 'vinodel';  	
GO

create login seller with password = 'seller'
create user seller from login seller
GO
sp_addrolemember @rolename = 'db_owner',  
    @membername = 'seller';  	
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

create table groundTypes
(
	ID tinyint not null identity(0,1),
	description nvarchar(256) not null
	primary key (ID)
);

create table resources
(
	ID smallint not null identity(0,1),
	description nvarchar(256) not null,
	groupID smallint,
	measure nvarchar(15),
	cost money,
	
	primary key (ID),
	foreign key (groupID) references resources
);

create table fields
(
	ID smallint not null identity(0,1),
	square float not null,
	precipitation float not null,
	seedingBy smallint,
	groundTypeID tinyint not null,
	placeID tinyint not null

	primary key (ID),
	foreign key (groundTypeID) references groundTypes,
	foreign key (placeID) references places,
	foreign key (seedingBy) references resources
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
	isSended bit not null default(0)

	primary key (ID),
	foreign key (resourceID) references resources,
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

delete from fields;
delete from users;
delete from userTypes;
delete from groundTypes;
delete from transactions;
delete from batches;
delete from places;
delete from resources;
delete from transactionsType;
GO

insert into transactionsType(description)
values ('Приём товара'), ('Списание товара'), ('Продажа товара'), ('Запрос на перемещение товара'), ('Подтверждение перемещения товара');
GO

insert into places(description)
values ('Склад'), ('Магазин'), ('Поле чудес'), ('Поле комплексных чисел'), ('Производственный цех');
GO

insert into groundTypes(description)
values ('Песчаная'), ('Супесчаная'), ('Глинистая'), ('Суглинистая');
GO

insert into fields(placeID, square, precipitation, groundTypeID)
values	(2, 200, 367, 2), 
		(3, 232.4, 368, 1);
GO

insert into userTypes(description)
values ('Винодел'), ('Менеджер'), ('Продавец'), ('Завхоз'), ('Администратор');
GO

insert into resources(description, groupID, measure, cost)
values	('Виноград', null, 'кг', null), 
		('Мерло', 0, 'кг', 1.2), 
		('Шардонне', 0, 'кг', 2.4), 
		('Рислинг', 0, 'кг', 1.4), 
		('Совиньон', 0, 'кг', 1.52);
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

	set @ResourceID = (select top(1) ID from resources where description like @ResourceName);
	set @PlaceID = (select top(1) ID from places where description like @PlaceName);
	set @TransactionTypeID = (select top(1) ID from transactionsType where description like 'Приём товара');
	if (@Cost is null) set @Cost = @Count * (select top(1) cost from resources where description like @ResourceName);

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

IF OBJECT_ID(N'dbo.SendBatchTo', N'U') IS NOT NULL 
drop procedure dbo.SendBatchTo
GO
create procedure dbo.SendBatchTo
	@BatchId int,
	@SubbjectName nvarchar(256)
	AS
BEGIN
	SET NOCOUNT ON;

	if((select top(1) isSended from batches where ID = @BatchId) != 0)
		return 3;

	DECLARE @TransactionTypeID smallint;	
	DECLARE @Seller smallint;	

	set @TransactionTypeID = (select top(1) ID from transactionsType where description like 'Запрос на перемещение товара');
	set @Seller = USER_ID(@SubbjectName)

	if ((@Seller is null) or (@TransactionTypeID is null)) return 1;

	begin transaction	
	update batches 
	set isSended = 1
	where ID = @BatchId

	if (@@ROWCOUNT = 0) 
	BEGIN
		rollback transaction
		return 2;
	END

	insert into transactions(batchId, typeID, date, subject, object)
	values (@BatchId, @TransactionTypeID, CURRENT_TIMESTAMP, USER_ID(CURRENT_USER), @Seller)

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

IF OBJECT_ID(N'dbo.SetFieldInfo', N'U') IS NOT NULL 
drop procedure dbo.SetFieldInfo
GO
create procedure dbo.SetFieldInfo
	@OldName nvarchar(256),
	@Name nvarchar(256) = null,
	@Square float = null,
	@Precipitation float = null,
	@GroundType nvarchar(256) = null,
	@SeedingBy nvarchar(256) = null
	AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @PlaceID smallint;
	DECLARE @GroundTypeID smallint;
	DECLARE @SeedingByID smallint;

	set @PlaceID = (select top(1) ID from places where description like @OldName);
		
	if (@PlaceID is null) return 1;

	if(@GroundType is null)
		set @GroundTypeID = (select top(1) groundTypeID from fields where placeID = @PlaceID);
	else
		set @GroundTypeID = (select top(1) ID from groundTypes where description like @GroundType);

	if (@GroundTypeID is null) return 1;

	if (@Square is null)
		set @Square = (select top(1) square from fields where placeID = @PlaceID);

	if (@Precipitation is null)
		set @Precipitation = (select top(1) precipitation from fields where placeID = @PlaceID);

	if (@SeedingBy is not null)
		set @SeedingByID = (select top(1) ID from resources where description = @SeedingBy);

	begin transaction	
	update fields
	set square = @Square, precipitation = @Precipitation, groundTypeID = @GroundTypeID, seedingBy = @SeedingByID
	where placeID = @PlaceID;

	if (@@ROWCOUNT = 0) 
	BEGIN
		rollback transaction
		return 2;
	END

	if (@Name is not null and @OldName != @Name)
	BEGIN
		update places
		set description = @Name
		where description like @OldName;

		if (@@ROWCOUNT = 0) 
		BEGIN
			rollback transaction
			return 3;
		END
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
SELECT      dbo.resources.description AS [resource type], 
			dbo.batches.count, dbo.resources.measure, 
			dbo.batches.description, dbo.batches.cost, 
			dbo.batches.productionDate, 
			dbo.places.description AS [place name]

FROM            dbo.batches INNER JOIN
                dbo.resources ON dbo.batches.resourceID = dbo.resources.ID INNER JOIN
                dbo.places ON dbo.batches.placeID = dbo.places.ID
GO

IF OBJECT_ID(N'[dbo].[UsefullTransactions]', N'U') IS NOT NULL 
DROP VIEW [dbo].[UsefullTransactions]
GO
CREATE VIEW [dbo].[UsefullTransactions]
AS
SELECT      dbo.transactions.date, 
			dbo.transactionsType.description AS action, 
			dbo.resources.description AS resource, 
			dbo.batches.count, dbo.resources.measure, 
			sys.sysusers.name AS subject, 
			sysusers_1.name AS object

FROM            dbo.transactions INNER JOIN
				dbo.transactionsType ON dbo.transactions.typeID = dbo.transactionsType.ID INNER JOIN
				dbo.batches ON dbo.transactions.batchID = dbo.batches.ID INNER JOIN
				dbo.resources ON dbo.batches.resourceID = dbo.resources.ID INNER JOIN
				sys.sysusers ON dbo.transactions.subject = sys.sysusers.uid LEFT JOIN
 				sys.sysusers AS sysusers_1 ON dbo.transactions.object = sysusers_1.uid
GO

IF OBJECT_ID(N'[dbo].[UsefullFields]', N'U') IS NOT NULL 
DROP VIEW [dbo].[UsefullFields]
GO
CREATE VIEW [dbo].[UsefullFields]
AS
SELECT      dbo.places.description AS [description], 
			dbo.fields.square,
			dbo.fields.precipitation,
			dbo.groundTypes.description as [ground type],
			dbo.resources.description as [seeding by]

FROM            dbo.fields INNER JOIN
                dbo.places ON dbo.fields.placeID = dbo.places.ID INNER JOIN
                dbo.groundTypes ON dbo.fields.groundTypeID = dbo.groundTypes.ID LEFT JOIN
				dbo.resources ON dbo.fields.seedingBy = dbo.resources.ID
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
		@ProductionDate = '2016.20.06',
		@Description = 'Вот и партия мерла. Я от счастья умерла'
GO
EXEC	[dbo].[AddBatch]
		@ResourceName = 'Совиньон',
		@Count = 3,
		@Cost = 45,
		@PlaceName = 'Склад',
		@ProductionDate = '2016.20.06',
		@Description = 'Нахрена мне пить бульон, я купила совиньон!'
GO
EXEC	[dbo].[AddBatch]
		@ResourceName = 'Шардонне',
		@Count = 10,
		@Cost = 60,
		@PlaceName = 'Поле чудес',
		@ProductionDate = '2016.20.06',
		@Description = 'Рислинг лучше шардонне, но налейте оба мне'
GO