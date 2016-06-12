IF OBJECT_ID(N'vProvance', N'U') IS NOT NULL 
DROP DATABASE [vProvance]
GO

CREATE DATABASE vProvance
GO
 
USE vProvance;

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


insert into transactionsType(description)
values ('Приём товара'), ('Списание товара'), ('Продажа товара'), ('Перемещение товара');
GO

insert into places(description)
values ('Склад'), ('Магазин'), ('Поле'), ('Производственный цех');
GO

insert into resourses(description, groupID, measure)
values ('Виноград', null, 'кг'), ('Мерло', 0, 'кг'), ('Шардонне', 0, 'кг'), ('Рислинг', 0, 'кг');
GO


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


EXEC	[dbo].[AddBatch]
		@ResourceName = 'Мерло',
		@Count = 2,
		@Cost = 23,
		@PlaceName = 'Склад',
		@ProductionDate = '2015.20.12',
		@Description = 'Вот и партия мерла. Я от счастья умерла'
GO