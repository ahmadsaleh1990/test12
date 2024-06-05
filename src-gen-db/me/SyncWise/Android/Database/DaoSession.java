package me.SyncWise.Android.Database;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.IdentityScopeType;

import me.SyncWise.Android.Database.Banks;
import me.SyncWise.Android.Database.Areas;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.AreaLevels;
import me.SyncWise.Android.Database.Companies;
import me.SyncWise.Android.Database.Permissions;
import me.SyncWise.Android.Database.NewClients;
import me.SyncWise.Android.Database.NewClientImages;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.UserPasswords;
import me.SyncWise.Android.Database.MsgHeader;
import me.SyncWise.Android.Database.UserPermissions;
import me.SyncWise.Android.Database.UserAccountNumbers;
import me.SyncWise.Android.Database.Printers;
import me.SyncWise.Android.Database.UserLimits;
import me.SyncWise.Android.Database.MsgNote;
import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.Cycles;
import me.SyncWise.Android.Database.Taxes;
import me.SyncWise.Android.Database.Units;
import me.SyncWise.Android.Database.Prices;
import me.SyncWise.Android.Database.ClientBarcodes;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.UserDivisions;
import me.SyncWise.Android.Database.ItemDivisions;
import me.SyncWise.Android.Database.ItemVolumes;
import me.SyncWise.Android.Database.ClientAreas;
import me.SyncWise.Android.Database.ClientItemCodes;
import me.SyncWise.Android.Database.ClientContacts;
import me.SyncWise.Android.Database.ClientPropertyValues;
import me.SyncWise.Android.Database.UserCycles;
import me.SyncWise.Android.Database.ItemBarcodes;
import me.SyncWise.Android.Database.ClientProperties;
import me.SyncWise.Android.Database.ClientPriceLists;
import me.SyncWise.Android.Database.PriceLists;
import me.SyncWise.Android.Database.ObjectiveAssignments;
import me.SyncWise.Android.Database.CycleCalls;
import me.SyncWise.Android.Database.ClientCurrencies;
import me.SyncWise.Android.Database.ClientPropertyLevels;
import me.SyncWise.Android.Database.ClientItemHistory;
import me.SyncWise.Android.Database.AssetsStatus;
import me.SyncWise.Android.Database.ClientDivisions;
import me.SyncWise.Android.Database.ClientAvailabilities;
import me.SyncWise.Android.Database.ClientMustStockList;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.CompetitorItems;
import me.SyncWise.Android.Database.PaymentTerms;
import me.SyncWise.Android.Database.BlankVisits;
import me.SyncWise.Android.Database.Journeys;
import me.SyncWise.Android.Database.BlankVisitContacts;
import me.SyncWise.Android.Database.VisitHistory;
import me.SyncWise.Android.Database.VisitReasons;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.VisitTypes;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.ClientDocumentImages;
import me.SyncWise.Android.Database.ClientStockCountDetails;
import me.SyncWise.Android.Database.ClientItemClassificationHistory;
import me.SyncWise.Android.Database.WareHouseBarcodeScanning;
import me.SyncWise.Android.Database.ClientSellingSuggestion;
import me.SyncWise.Android.Database.TransactionPromotionDetails;
import me.SyncWise.Android.Database.TransactionDetailsMissedMSL;
import me.SyncWise.Android.Database.ClientStockCountHeaders;
import me.SyncWise.Android.Database.DailyTargetAchievements;
import me.SyncWise.Android.Database.ClientItemClassificationLevels;
import me.SyncWise.Android.Database.ClientItemClassifications;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.SuggestedUserLoad;
import me.SyncWise.Android.Database.PromotionHeaders;
import me.SyncWise.Android.Database.SurveyAssignments;
import me.SyncWise.Android.Database.SurveyQuestions;
import me.SyncWise.Android.Database.SubQuestionAnswers;
import me.SyncWise.Android.Database.MovementDetails;
import me.SyncWise.Android.Database.PromotionAssignments;
import me.SyncWise.Android.Database.Vehicles;
import me.SyncWise.Android.Database.DeviceTracking;
import me.SyncWise.Android.Database.VehiclesStock;
import me.SyncWise.Android.Database.WareHouseBarcodes;
import me.SyncWise.Android.Database.TransactionSequences;
import me.SyncWise.Android.Database.SurveyAnswers;
import me.SyncWise.Android.Database.PromotionDetails;
import me.SyncWise.Android.Database.QuestionAnswers;
import me.SyncWise.Android.Database.MovementHeaders;
import me.SyncWise.Android.Database.SurveyImages;
import me.SyncWise.Android.Database.DeviceSerials;
import me.SyncWise.Android.Database.Surveys;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.ClientUserCollection;
import me.SyncWise.Android.Database.CollectionInvoices;
import me.SyncWise.Android.Database.TotalClientDues;
import me.SyncWise.Android.Database.WarehouseQuantities;
import me.SyncWise.Android.Database.MoneyCategory;
import me.SyncWise.Android.Database.CollectionDetails;
import me.SyncWise.Android.Database.CollectionImages;
import me.SyncWise.Android.Database.ClientDues;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.Brands;
import me.SyncWise.Android.Database.ClientAssetsPictures;
import me.SyncWise.Android.Database.ShareOfShelfTracker;
import me.SyncWise.Android.Database.SOSTrackerHeaders;
import me.SyncWise.Android.Database.UserVersions;
import me.SyncWise.Android.Database.UserSpecialPriceLists;
import me.SyncWise.Android.Database.ForceSyncJourneys;
import me.SyncWise.Android.Database.NewSkuListDivisions;
import me.SyncWise.Android.Database.SOSTargetAssignment;
import me.SyncWise.Android.Database.ClientMouvementStock;
import me.SyncWise.Android.Database.SOSTrackerDetails;
import me.SyncWise.Android.Database.SOSTargetHeaders;
import me.SyncWise.Android.Database.DuoUsers;
import me.SyncWise.Android.Database.ClientNewSkuList;
import me.SyncWise.Android.Database.Objectives;
import me.SyncWise.Android.Database.SOSBrands;
import me.SyncWise.Android.Database.SOSCategoryBrands;
import me.SyncWise.Android.Database.MSLDivisions;
import me.SyncWise.Android.Database.ShareOfShelves;
import me.SyncWise.Android.Database.SOSCategories;
import me.SyncWise.Android.Database.SOSTargetDetails;
import me.SyncWise.Android.Database.ClientAssetStatus;
import me.SyncWise.Android.Database.ClientCreditings;
import me.SyncWise.Android.Database.TargetAssignments;
import me.SyncWise.Android.Database.ObjectivePriorities;
import me.SyncWise.Android.Database.TargetHeaders;
import me.SyncWise.Android.Database.TargetDetails;
import me.SyncWise.Android.Database.ObjectiveAchievements;
import me.SyncWise.Android.Database.TargetAchievements;

import me.SyncWise.Android.Database.BanksDao;
import me.SyncWise.Android.Database.AreasDao;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.ConnectionSettingsDao;
import me.SyncWise.Android.Database.AreaLevelsDao;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.PermissionsDao;
import me.SyncWise.Android.Database.NewClientsDao;
import me.SyncWise.Android.Database.NewClientImagesDao;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.UserPasswordsDao;
import me.SyncWise.Android.Database.MsgHeaderDao;
import me.SyncWise.Android.Database.UserPermissionsDao;
import me.SyncWise.Android.Database.UserAccountNumbersDao;
import me.SyncWise.Android.Database.PrintersDao;
import me.SyncWise.Android.Database.UserLimitsDao;
import me.SyncWise.Android.Database.MsgNoteDao;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.CyclesDao;
import me.SyncWise.Android.Database.TaxesDao;
import me.SyncWise.Android.Database.UnitsDao;
import me.SyncWise.Android.Database.PricesDao;
import me.SyncWise.Android.Database.ClientBarcodesDao;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.UserDivisionsDao;
import me.SyncWise.Android.Database.ItemDivisionsDao;
import me.SyncWise.Android.Database.ItemVolumesDao;
import me.SyncWise.Android.Database.ClientAreasDao;
import me.SyncWise.Android.Database.ClientItemCodesDao;
import me.SyncWise.Android.Database.ClientContactsDao;
import me.SyncWise.Android.Database.ClientPropertyValuesDao;
import me.SyncWise.Android.Database.UserCyclesDao;
import me.SyncWise.Android.Database.ItemBarcodesDao;
import me.SyncWise.Android.Database.ClientPropertiesDao;
import me.SyncWise.Android.Database.ClientPriceListsDao;
import me.SyncWise.Android.Database.PriceListsDao;
import me.SyncWise.Android.Database.ObjectiveAssignmentsDao;
import me.SyncWise.Android.Database.CycleCallsDao;
import me.SyncWise.Android.Database.ClientCurrenciesDao;
import me.SyncWise.Android.Database.ClientPropertyLevelsDao;
import me.SyncWise.Android.Database.ClientItemHistoryDao;
import me.SyncWise.Android.Database.AssetsStatusDao;
import me.SyncWise.Android.Database.ClientDivisionsDao;
import me.SyncWise.Android.Database.ClientAvailabilitiesDao;
import me.SyncWise.Android.Database.ClientMustStockListDao;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.CompetitorItemsDao;
import me.SyncWise.Android.Database.PaymentTermsDao;
import me.SyncWise.Android.Database.BlankVisitsDao;
import me.SyncWise.Android.Database.JourneysDao;
import me.SyncWise.Android.Database.BlankVisitContactsDao;
import me.SyncWise.Android.Database.VisitHistoryDao;
import me.SyncWise.Android.Database.VisitReasonsDao;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Database.VisitTypesDao;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.ClientDocumentImagesDao;
import me.SyncWise.Android.Database.ClientStockCountDetailsDao;
import me.SyncWise.Android.Database.ClientItemClassificationHistoryDao;
import me.SyncWise.Android.Database.WareHouseBarcodeScanningDao;
import me.SyncWise.Android.Database.ClientSellingSuggestionDao;
import me.SyncWise.Android.Database.TransactionPromotionDetailsDao;
import me.SyncWise.Android.Database.TransactionDetailsMissedMSLDao;
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
import me.SyncWise.Android.Database.DailyTargetAchievementsDao;
import me.SyncWise.Android.Database.ClientItemClassificationLevelsDao;
import me.SyncWise.Android.Database.ClientItemClassificationsDao;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Database.SuggestedUserLoadDao;
import me.SyncWise.Android.Database.PromotionHeadersDao;
import me.SyncWise.Android.Database.SurveyAssignmentsDao;
import me.SyncWise.Android.Database.SurveyQuestionsDao;
import me.SyncWise.Android.Database.SubQuestionAnswersDao;
import me.SyncWise.Android.Database.MovementDetailsDao;
import me.SyncWise.Android.Database.PromotionAssignmentsDao;
import me.SyncWise.Android.Database.VehiclesDao;
import me.SyncWise.Android.Database.DeviceTrackingDao;
import me.SyncWise.Android.Database.VehiclesStockDao;
import me.SyncWise.Android.Database.WareHouseBarcodesDao;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.SurveyAnswersDao;
import me.SyncWise.Android.Database.PromotionDetailsDao;
import me.SyncWise.Android.Database.QuestionAnswersDao;
import me.SyncWise.Android.Database.MovementHeadersDao;
import me.SyncWise.Android.Database.SurveyImagesDao;
import me.SyncWise.Android.Database.DeviceSerialsDao;
import me.SyncWise.Android.Database.SurveysDao;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.ClientUserCollectionDao;
import me.SyncWise.Android.Database.CollectionInvoicesDao;
import me.SyncWise.Android.Database.TotalClientDuesDao;
import me.SyncWise.Android.Database.WarehouseQuantitiesDao;
import me.SyncWise.Android.Database.MoneyCategoryDao;
import me.SyncWise.Android.Database.CollectionDetailsDao;
import me.SyncWise.Android.Database.CollectionImagesDao;
import me.SyncWise.Android.Database.ClientDuesDao;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.BrandsDao;
import me.SyncWise.Android.Database.ClientAssetsPicturesDao;
import me.SyncWise.Android.Database.ShareOfShelfTrackerDao;
import me.SyncWise.Android.Database.SOSTrackerHeadersDao;
import me.SyncWise.Android.Database.UserVersionsDao;
import me.SyncWise.Android.Database.UserSpecialPriceListsDao;
import me.SyncWise.Android.Database.ForceSyncJourneysDao;
import me.SyncWise.Android.Database.NewSkuListDivisionsDao;
import me.SyncWise.Android.Database.SOSTargetAssignmentDao;
import me.SyncWise.Android.Database.ClientMouvementStockDao;
import me.SyncWise.Android.Database.SOSTrackerDetailsDao;
import me.SyncWise.Android.Database.SOSTargetHeadersDao;
import me.SyncWise.Android.Database.DuoUsersDao;
import me.SyncWise.Android.Database.ClientNewSkuListDao;
import me.SyncWise.Android.Database.ObjectivesDao;
import me.SyncWise.Android.Database.SOSBrandsDao;
import me.SyncWise.Android.Database.SOSCategoryBrandsDao;
import me.SyncWise.Android.Database.MSLDivisionsDao;
import me.SyncWise.Android.Database.ShareOfShelvesDao;
import me.SyncWise.Android.Database.SOSCategoriesDao;
import me.SyncWise.Android.Database.SOSTargetDetailsDao;
import me.SyncWise.Android.Database.ClientAssetStatusDao;
import me.SyncWise.Android.Database.ClientCreditingsDao;
import me.SyncWise.Android.Database.TargetAssignmentsDao;
import me.SyncWise.Android.Database.ObjectivePrioritiesDao;
import me.SyncWise.Android.Database.TargetHeadersDao;
import me.SyncWise.Android.Database.TargetDetailsDao;
import me.SyncWise.Android.Database.ObjectiveAchievementsDao;
import me.SyncWise.Android.Database.TargetAchievementsDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig banksDaoConfig;
    private final DaoConfig areasDaoConfig;
    private final DaoConfig usersDaoConfig;
    private final DaoConfig connectionSettingsDaoConfig;
    private final DaoConfig areaLevelsDaoConfig;
    private final DaoConfig companiesDaoConfig;
    private final DaoConfig permissionsDaoConfig;
    private final DaoConfig newClientsDaoConfig;
    private final DaoConfig newClientImagesDaoConfig;
    private final DaoConfig clientsDaoConfig;
    private final DaoConfig userPasswordsDaoConfig;
    private final DaoConfig msgHeaderDaoConfig;
    private final DaoConfig userPermissionsDaoConfig;
    private final DaoConfig userAccountNumbersDaoConfig;
    private final DaoConfig printersDaoConfig;
    private final DaoConfig userLimitsDaoConfig;
    private final DaoConfig msgNoteDaoConfig;
    private final DaoConfig itemsDaoConfig;
    private final DaoConfig cyclesDaoConfig;
    private final DaoConfig taxesDaoConfig;
    private final DaoConfig unitsDaoConfig;
    private final DaoConfig pricesDaoConfig;
    private final DaoConfig clientBarcodesDaoConfig;
    private final DaoConfig divisionsDaoConfig;
    private final DaoConfig userDivisionsDaoConfig;
    private final DaoConfig itemDivisionsDaoConfig;
    private final DaoConfig itemVolumesDaoConfig;
    private final DaoConfig clientAreasDaoConfig;
    private final DaoConfig clientItemCodesDaoConfig;
    private final DaoConfig clientContactsDaoConfig;
    private final DaoConfig clientPropertyValuesDaoConfig;
    private final DaoConfig userCyclesDaoConfig;
    private final DaoConfig itemBarcodesDaoConfig;
    private final DaoConfig clientPropertiesDaoConfig;
    private final DaoConfig clientPriceListsDaoConfig;
    private final DaoConfig priceListsDaoConfig;
    private final DaoConfig objectiveAssignmentsDaoConfig;
    private final DaoConfig cycleCallsDaoConfig;
    private final DaoConfig clientCurrenciesDaoConfig;
    private final DaoConfig clientPropertyLevelsDaoConfig;
    private final DaoConfig clientItemHistoryDaoConfig;
    private final DaoConfig assetsStatusDaoConfig;
    private final DaoConfig clientDivisionsDaoConfig;
    private final DaoConfig clientAvailabilitiesDaoConfig;
    private final DaoConfig clientMustStockListDaoConfig;
    private final DaoConfig currenciesDaoConfig;
    private final DaoConfig competitorItemsDaoConfig;
    private final DaoConfig paymentTermsDaoConfig;
    private final DaoConfig blankVisitsDaoConfig;
    private final DaoConfig journeysDaoConfig;
    private final DaoConfig blankVisitContactsDaoConfig;
    private final DaoConfig visitHistoryDaoConfig;
    private final DaoConfig visitReasonsDaoConfig;
    private final DaoConfig reasonsDaoConfig;
    private final DaoConfig visitTypesDaoConfig;
    private final DaoConfig transactionHeadersDaoConfig;
    private final DaoConfig clientDocumentImagesDaoConfig;
    private final DaoConfig clientStockCountDetailsDaoConfig;
    private final DaoConfig clientItemClassificationHistoryDaoConfig;
    private final DaoConfig wareHouseBarcodeScanningDaoConfig;
    private final DaoConfig clientSellingSuggestionDaoConfig;
    private final DaoConfig transactionPromotionDetailsDaoConfig;
    private final DaoConfig transactionDetailsMissedMSLDaoConfig;
    private final DaoConfig clientStockCountHeadersDaoConfig;
    private final DaoConfig dailyTargetAchievementsDaoConfig;
    private final DaoConfig clientItemClassificationLevelsDaoConfig;
    private final DaoConfig clientItemClassificationsDaoConfig;
    private final DaoConfig visitsDaoConfig;
    private final DaoConfig suggestedUserLoadDaoConfig;
    private final DaoConfig promotionHeadersDaoConfig;
    private final DaoConfig surveyAssignmentsDaoConfig;
    private final DaoConfig surveyQuestionsDaoConfig;
    private final DaoConfig subQuestionAnswersDaoConfig;
    private final DaoConfig movementDetailsDaoConfig;
    private final DaoConfig promotionAssignmentsDaoConfig;
    private final DaoConfig vehiclesDaoConfig;
    private final DaoConfig deviceTrackingDaoConfig;
    private final DaoConfig vehiclesStockDaoConfig;
    private final DaoConfig wareHouseBarcodesDaoConfig;
    private final DaoConfig transactionSequencesDaoConfig;
    private final DaoConfig surveyAnswersDaoConfig;
    private final DaoConfig promotionDetailsDaoConfig;
    private final DaoConfig questionAnswersDaoConfig;
    private final DaoConfig movementHeadersDaoConfig;
    private final DaoConfig surveyImagesDaoConfig;
    private final DaoConfig deviceSerialsDaoConfig;
    private final DaoConfig surveysDaoConfig;
    private final DaoConfig transactionDetailsDaoConfig;
    private final DaoConfig clientUserCollectionDaoConfig;
    private final DaoConfig collectionInvoicesDaoConfig;
    private final DaoConfig totalClientDuesDaoConfig;
    private final DaoConfig warehouseQuantitiesDaoConfig;
    private final DaoConfig moneyCategoryDaoConfig;
    private final DaoConfig collectionDetailsDaoConfig;
    private final DaoConfig collectionImagesDaoConfig;
    private final DaoConfig clientDuesDaoConfig;
    private final DaoConfig collectionHeadersDaoConfig;
    private final DaoConfig brandsDaoConfig;
    private final DaoConfig clientAssetsPicturesDaoConfig;
    private final DaoConfig shareOfShelfTrackerDaoConfig;
    private final DaoConfig sOSTrackerHeadersDaoConfig;
    private final DaoConfig userVersionsDaoConfig;
    private final DaoConfig userSpecialPriceListsDaoConfig;
    private final DaoConfig forceSyncJourneysDaoConfig;
    private final DaoConfig newSkuListDivisionsDaoConfig;
    private final DaoConfig sOSTargetAssignmentDaoConfig;
    private final DaoConfig clientMouvementStockDaoConfig;
    private final DaoConfig sOSTrackerDetailsDaoConfig;
    private final DaoConfig sOSTargetHeadersDaoConfig;
    private final DaoConfig duoUsersDaoConfig;
    private final DaoConfig clientNewSkuListDaoConfig;
    private final DaoConfig objectivesDaoConfig;
    private final DaoConfig sOSBrandsDaoConfig;
    private final DaoConfig sOSCategoryBrandsDaoConfig;
    private final DaoConfig mSLDivisionsDaoConfig;
    private final DaoConfig shareOfShelvesDaoConfig;
    private final DaoConfig sOSCategoriesDaoConfig;
    private final DaoConfig sOSTargetDetailsDaoConfig;
    private final DaoConfig clientAssetStatusDaoConfig;
    private final DaoConfig clientCreditingsDaoConfig;
    private final DaoConfig targetAssignmentsDaoConfig;
    private final DaoConfig objectivePrioritiesDaoConfig;
    private final DaoConfig targetHeadersDaoConfig;
    private final DaoConfig targetDetailsDaoConfig;
    private final DaoConfig objectiveAchievementsDaoConfig;
    private final DaoConfig targetAchievementsDaoConfig;

    private final BanksDao banksDao;
    private final AreasDao areasDao;
    private final UsersDao usersDao;
    private final ConnectionSettingsDao connectionSettingsDao;
    private final AreaLevelsDao areaLevelsDao;
    private final CompaniesDao companiesDao;
    private final PermissionsDao permissionsDao;
    private final NewClientsDao newClientsDao;
    private final NewClientImagesDao newClientImagesDao;
    private final ClientsDao clientsDao;
    private final UserPasswordsDao userPasswordsDao;
    private final MsgHeaderDao msgHeaderDao;
    private final UserPermissionsDao userPermissionsDao;
    private final UserAccountNumbersDao userAccountNumbersDao;
    private final PrintersDao printersDao;
    private final UserLimitsDao userLimitsDao;
    private final MsgNoteDao msgNoteDao;
    private final ItemsDao itemsDao;
    private final CyclesDao cyclesDao;
    private final TaxesDao taxesDao;
    private final UnitsDao unitsDao;
    private final PricesDao pricesDao;
    private final ClientBarcodesDao clientBarcodesDao;
    private final DivisionsDao divisionsDao;
    private final UserDivisionsDao userDivisionsDao;
    private final ItemDivisionsDao itemDivisionsDao;
    private final ItemVolumesDao itemVolumesDao;
    private final ClientAreasDao clientAreasDao;
    private final ClientItemCodesDao clientItemCodesDao;
    private final ClientContactsDao clientContactsDao;
    private final ClientPropertyValuesDao clientPropertyValuesDao;
    private final UserCyclesDao userCyclesDao;
    private final ItemBarcodesDao itemBarcodesDao;
    private final ClientPropertiesDao clientPropertiesDao;
    private final ClientPriceListsDao clientPriceListsDao;
    private final PriceListsDao priceListsDao;
    private final ObjectiveAssignmentsDao objectiveAssignmentsDao;
    private final CycleCallsDao cycleCallsDao;
    private final ClientCurrenciesDao clientCurrenciesDao;
    private final ClientPropertyLevelsDao clientPropertyLevelsDao;
    private final ClientItemHistoryDao clientItemHistoryDao;
    private final AssetsStatusDao assetsStatusDao;
    private final ClientDivisionsDao clientDivisionsDao;
    private final ClientAvailabilitiesDao clientAvailabilitiesDao;
    private final ClientMustStockListDao clientMustStockListDao;
    private final CurrenciesDao currenciesDao;
    private final CompetitorItemsDao competitorItemsDao;
    private final PaymentTermsDao paymentTermsDao;
    private final BlankVisitsDao blankVisitsDao;
    private final JourneysDao journeysDao;
    private final BlankVisitContactsDao blankVisitContactsDao;
    private final VisitHistoryDao visitHistoryDao;
    private final VisitReasonsDao visitReasonsDao;
    private final ReasonsDao reasonsDao;
    private final VisitTypesDao visitTypesDao;
    private final TransactionHeadersDao transactionHeadersDao;
    private final ClientDocumentImagesDao clientDocumentImagesDao;
    private final ClientStockCountDetailsDao clientStockCountDetailsDao;
    private final ClientItemClassificationHistoryDao clientItemClassificationHistoryDao;
    private final WareHouseBarcodeScanningDao wareHouseBarcodeScanningDao;
    private final ClientSellingSuggestionDao clientSellingSuggestionDao;
    private final TransactionPromotionDetailsDao transactionPromotionDetailsDao;
    private final TransactionDetailsMissedMSLDao transactionDetailsMissedMSLDao;
    private final ClientStockCountHeadersDao clientStockCountHeadersDao;
    private final DailyTargetAchievementsDao dailyTargetAchievementsDao;
    private final ClientItemClassificationLevelsDao clientItemClassificationLevelsDao;
    private final ClientItemClassificationsDao clientItemClassificationsDao;
    private final VisitsDao visitsDao;
    private final SuggestedUserLoadDao suggestedUserLoadDao;
    private final PromotionHeadersDao promotionHeadersDao;
    private final SurveyAssignmentsDao surveyAssignmentsDao;
    private final SurveyQuestionsDao surveyQuestionsDao;
    private final SubQuestionAnswersDao subQuestionAnswersDao;
    private final MovementDetailsDao movementDetailsDao;
    private final PromotionAssignmentsDao promotionAssignmentsDao;
    private final VehiclesDao vehiclesDao;
    private final DeviceTrackingDao deviceTrackingDao;
    private final VehiclesStockDao vehiclesStockDao;
    private final WareHouseBarcodesDao wareHouseBarcodesDao;
    private final TransactionSequencesDao transactionSequencesDao;
    private final SurveyAnswersDao surveyAnswersDao;
    private final PromotionDetailsDao promotionDetailsDao;
    private final QuestionAnswersDao questionAnswersDao;
    private final MovementHeadersDao movementHeadersDao;
    private final SurveyImagesDao surveyImagesDao;
    private final DeviceSerialsDao deviceSerialsDao;
    private final SurveysDao surveysDao;
    private final TransactionDetailsDao transactionDetailsDao;
    private final ClientUserCollectionDao clientUserCollectionDao;
    private final CollectionInvoicesDao collectionInvoicesDao;
    private final TotalClientDuesDao totalClientDuesDao;
    private final WarehouseQuantitiesDao warehouseQuantitiesDao;
    private final MoneyCategoryDao moneyCategoryDao;
    private final CollectionDetailsDao collectionDetailsDao;
    private final CollectionImagesDao collectionImagesDao;
    private final ClientDuesDao clientDuesDao;
    private final CollectionHeadersDao collectionHeadersDao;
    private final BrandsDao brandsDao;
    private final ClientAssetsPicturesDao clientAssetsPicturesDao;
    private final ShareOfShelfTrackerDao shareOfShelfTrackerDao;
    private final SOSTrackerHeadersDao sOSTrackerHeadersDao;
    private final UserVersionsDao userVersionsDao;
    private final UserSpecialPriceListsDao userSpecialPriceListsDao;
    private final ForceSyncJourneysDao forceSyncJourneysDao;
    private final NewSkuListDivisionsDao newSkuListDivisionsDao;
    private final SOSTargetAssignmentDao sOSTargetAssignmentDao;
    private final ClientMouvementStockDao clientMouvementStockDao;
    private final SOSTrackerDetailsDao sOSTrackerDetailsDao;
    private final SOSTargetHeadersDao sOSTargetHeadersDao;
    private final DuoUsersDao duoUsersDao;
    private final ClientNewSkuListDao clientNewSkuListDao;
    private final ObjectivesDao objectivesDao;
    private final SOSBrandsDao sOSBrandsDao;
    private final SOSCategoryBrandsDao sOSCategoryBrandsDao;
    private final MSLDivisionsDao mSLDivisionsDao;
    private final ShareOfShelvesDao shareOfShelvesDao;
    private final SOSCategoriesDao sOSCategoriesDao;
    private final SOSTargetDetailsDao sOSTargetDetailsDao;
    private final ClientAssetStatusDao clientAssetStatusDao;
    private final ClientCreditingsDao clientCreditingsDao;
    private final TargetAssignmentsDao targetAssignmentsDao;
    private final ObjectivePrioritiesDao objectivePrioritiesDao;
    private final TargetHeadersDao targetHeadersDao;
    private final TargetDetailsDao targetDetailsDao;
    private final ObjectiveAchievementsDao objectiveAchievementsDao;
    private final TargetAchievementsDao targetAchievementsDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        banksDaoConfig = daoConfigMap.get(BanksDao.class).clone();
        banksDaoConfig.initIdentityScope(type);

        areasDaoConfig = daoConfigMap.get(AreasDao.class).clone();
        areasDaoConfig.initIdentityScope(type);

        usersDaoConfig = daoConfigMap.get(UsersDao.class).clone();
        usersDaoConfig.initIdentityScope(type);

        connectionSettingsDaoConfig = daoConfigMap.get(ConnectionSettingsDao.class).clone();
        connectionSettingsDaoConfig.initIdentityScope(type);

        areaLevelsDaoConfig = daoConfigMap.get(AreaLevelsDao.class).clone();
        areaLevelsDaoConfig.initIdentityScope(type);

        companiesDaoConfig = daoConfigMap.get(CompaniesDao.class).clone();
        companiesDaoConfig.initIdentityScope(type);

        permissionsDaoConfig = daoConfigMap.get(PermissionsDao.class).clone();
        permissionsDaoConfig.initIdentityScope(type);

        newClientsDaoConfig = daoConfigMap.get(NewClientsDao.class).clone();
        newClientsDaoConfig.initIdentityScope(type);

        newClientImagesDaoConfig = daoConfigMap.get(NewClientImagesDao.class).clone();
        newClientImagesDaoConfig.initIdentityScope(type);

        clientsDaoConfig = daoConfigMap.get(ClientsDao.class).clone();
        clientsDaoConfig.initIdentityScope(type);

        userPasswordsDaoConfig = daoConfigMap.get(UserPasswordsDao.class).clone();
        userPasswordsDaoConfig.initIdentityScope(type);

        msgHeaderDaoConfig = daoConfigMap.get(MsgHeaderDao.class).clone();
        msgHeaderDaoConfig.initIdentityScope(type);

        userPermissionsDaoConfig = daoConfigMap.get(UserPermissionsDao.class).clone();
        userPermissionsDaoConfig.initIdentityScope(type);

        userAccountNumbersDaoConfig = daoConfigMap.get(UserAccountNumbersDao.class).clone();
        userAccountNumbersDaoConfig.initIdentityScope(type);

        printersDaoConfig = daoConfigMap.get(PrintersDao.class).clone();
        printersDaoConfig.initIdentityScope(type);

        userLimitsDaoConfig = daoConfigMap.get(UserLimitsDao.class).clone();
        userLimitsDaoConfig.initIdentityScope(type);

        msgNoteDaoConfig = daoConfigMap.get(MsgNoteDao.class).clone();
        msgNoteDaoConfig.initIdentityScope(type);

        itemsDaoConfig = daoConfigMap.get(ItemsDao.class).clone();
        itemsDaoConfig.initIdentityScope(type);

        cyclesDaoConfig = daoConfigMap.get(CyclesDao.class).clone();
        cyclesDaoConfig.initIdentityScope(type);

        taxesDaoConfig = daoConfigMap.get(TaxesDao.class).clone();
        taxesDaoConfig.initIdentityScope(type);

        unitsDaoConfig = daoConfigMap.get(UnitsDao.class).clone();
        unitsDaoConfig.initIdentityScope(type);

        pricesDaoConfig = daoConfigMap.get(PricesDao.class).clone();
        pricesDaoConfig.initIdentityScope(type);

        clientBarcodesDaoConfig = daoConfigMap.get(ClientBarcodesDao.class).clone();
        clientBarcodesDaoConfig.initIdentityScope(type);

        divisionsDaoConfig = daoConfigMap.get(DivisionsDao.class).clone();
        divisionsDaoConfig.initIdentityScope(type);

        userDivisionsDaoConfig = daoConfigMap.get(UserDivisionsDao.class).clone();
        userDivisionsDaoConfig.initIdentityScope(type);

        itemDivisionsDaoConfig = daoConfigMap.get(ItemDivisionsDao.class).clone();
        itemDivisionsDaoConfig.initIdentityScope(type);

        itemVolumesDaoConfig = daoConfigMap.get(ItemVolumesDao.class).clone();
        itemVolumesDaoConfig.initIdentityScope(type);

        clientAreasDaoConfig = daoConfigMap.get(ClientAreasDao.class).clone();
        clientAreasDaoConfig.initIdentityScope(type);

        clientItemCodesDaoConfig = daoConfigMap.get(ClientItemCodesDao.class).clone();
        clientItemCodesDaoConfig.initIdentityScope(type);

        clientContactsDaoConfig = daoConfigMap.get(ClientContactsDao.class).clone();
        clientContactsDaoConfig.initIdentityScope(type);

        clientPropertyValuesDaoConfig = daoConfigMap.get(ClientPropertyValuesDao.class).clone();
        clientPropertyValuesDaoConfig.initIdentityScope(type);

        userCyclesDaoConfig = daoConfigMap.get(UserCyclesDao.class).clone();
        userCyclesDaoConfig.initIdentityScope(type);

        itemBarcodesDaoConfig = daoConfigMap.get(ItemBarcodesDao.class).clone();
        itemBarcodesDaoConfig.initIdentityScope(type);

        clientPropertiesDaoConfig = daoConfigMap.get(ClientPropertiesDao.class).clone();
        clientPropertiesDaoConfig.initIdentityScope(type);

        clientPriceListsDaoConfig = daoConfigMap.get(ClientPriceListsDao.class).clone();
        clientPriceListsDaoConfig.initIdentityScope(type);

        priceListsDaoConfig = daoConfigMap.get(PriceListsDao.class).clone();
        priceListsDaoConfig.initIdentityScope(type);

        objectiveAssignmentsDaoConfig = daoConfigMap.get(ObjectiveAssignmentsDao.class).clone();
        objectiveAssignmentsDaoConfig.initIdentityScope(type);

        cycleCallsDaoConfig = daoConfigMap.get(CycleCallsDao.class).clone();
        cycleCallsDaoConfig.initIdentityScope(type);

        clientCurrenciesDaoConfig = daoConfigMap.get(ClientCurrenciesDao.class).clone();
        clientCurrenciesDaoConfig.initIdentityScope(type);

        clientPropertyLevelsDaoConfig = daoConfigMap.get(ClientPropertyLevelsDao.class).clone();
        clientPropertyLevelsDaoConfig.initIdentityScope(type);

        clientItemHistoryDaoConfig = daoConfigMap.get(ClientItemHistoryDao.class).clone();
        clientItemHistoryDaoConfig.initIdentityScope(type);

        assetsStatusDaoConfig = daoConfigMap.get(AssetsStatusDao.class).clone();
        assetsStatusDaoConfig.initIdentityScope(type);

        clientDivisionsDaoConfig = daoConfigMap.get(ClientDivisionsDao.class).clone();
        clientDivisionsDaoConfig.initIdentityScope(type);

        clientAvailabilitiesDaoConfig = daoConfigMap.get(ClientAvailabilitiesDao.class).clone();
        clientAvailabilitiesDaoConfig.initIdentityScope(type);

        clientMustStockListDaoConfig = daoConfigMap.get(ClientMustStockListDao.class).clone();
        clientMustStockListDaoConfig.initIdentityScope(type);

        currenciesDaoConfig = daoConfigMap.get(CurrenciesDao.class).clone();
        currenciesDaoConfig.initIdentityScope(type);

        competitorItemsDaoConfig = daoConfigMap.get(CompetitorItemsDao.class).clone();
        competitorItemsDaoConfig.initIdentityScope(type);

        paymentTermsDaoConfig = daoConfigMap.get(PaymentTermsDao.class).clone();
        paymentTermsDaoConfig.initIdentityScope(type);

        blankVisitsDaoConfig = daoConfigMap.get(BlankVisitsDao.class).clone();
        blankVisitsDaoConfig.initIdentityScope(type);

        journeysDaoConfig = daoConfigMap.get(JourneysDao.class).clone();
        journeysDaoConfig.initIdentityScope(type);

        blankVisitContactsDaoConfig = daoConfigMap.get(BlankVisitContactsDao.class).clone();
        blankVisitContactsDaoConfig.initIdentityScope(type);

        visitHistoryDaoConfig = daoConfigMap.get(VisitHistoryDao.class).clone();
        visitHistoryDaoConfig.initIdentityScope(type);

        visitReasonsDaoConfig = daoConfigMap.get(VisitReasonsDao.class).clone();
        visitReasonsDaoConfig.initIdentityScope(type);

        reasonsDaoConfig = daoConfigMap.get(ReasonsDao.class).clone();
        reasonsDaoConfig.initIdentityScope(type);

        visitTypesDaoConfig = daoConfigMap.get(VisitTypesDao.class).clone();
        visitTypesDaoConfig.initIdentityScope(type);

        transactionHeadersDaoConfig = daoConfigMap.get(TransactionHeadersDao.class).clone();
        transactionHeadersDaoConfig.initIdentityScope(type);

        clientDocumentImagesDaoConfig = daoConfigMap.get(ClientDocumentImagesDao.class).clone();
        clientDocumentImagesDaoConfig.initIdentityScope(type);

        clientStockCountDetailsDaoConfig = daoConfigMap.get(ClientStockCountDetailsDao.class).clone();
        clientStockCountDetailsDaoConfig.initIdentityScope(type);

        clientItemClassificationHistoryDaoConfig = daoConfigMap.get(ClientItemClassificationHistoryDao.class).clone();
        clientItemClassificationHistoryDaoConfig.initIdentityScope(type);

        wareHouseBarcodeScanningDaoConfig = daoConfigMap.get(WareHouseBarcodeScanningDao.class).clone();
        wareHouseBarcodeScanningDaoConfig.initIdentityScope(type);

        clientSellingSuggestionDaoConfig = daoConfigMap.get(ClientSellingSuggestionDao.class).clone();
        clientSellingSuggestionDaoConfig.initIdentityScope(type);

        transactionPromotionDetailsDaoConfig = daoConfigMap.get(TransactionPromotionDetailsDao.class).clone();
        transactionPromotionDetailsDaoConfig.initIdentityScope(type);

        transactionDetailsMissedMSLDaoConfig = daoConfigMap.get(TransactionDetailsMissedMSLDao.class).clone();
        transactionDetailsMissedMSLDaoConfig.initIdentityScope(type);

        clientStockCountHeadersDaoConfig = daoConfigMap.get(ClientStockCountHeadersDao.class).clone();
        clientStockCountHeadersDaoConfig.initIdentityScope(type);

        dailyTargetAchievementsDaoConfig = daoConfigMap.get(DailyTargetAchievementsDao.class).clone();
        dailyTargetAchievementsDaoConfig.initIdentityScope(type);

        clientItemClassificationLevelsDaoConfig = daoConfigMap.get(ClientItemClassificationLevelsDao.class).clone();
        clientItemClassificationLevelsDaoConfig.initIdentityScope(type);

        clientItemClassificationsDaoConfig = daoConfigMap.get(ClientItemClassificationsDao.class).clone();
        clientItemClassificationsDaoConfig.initIdentityScope(type);

        visitsDaoConfig = daoConfigMap.get(VisitsDao.class).clone();
        visitsDaoConfig.initIdentityScope(type);

        suggestedUserLoadDaoConfig = daoConfigMap.get(SuggestedUserLoadDao.class).clone();
        suggestedUserLoadDaoConfig.initIdentityScope(type);

        promotionHeadersDaoConfig = daoConfigMap.get(PromotionHeadersDao.class).clone();
        promotionHeadersDaoConfig.initIdentityScope(type);

        surveyAssignmentsDaoConfig = daoConfigMap.get(SurveyAssignmentsDao.class).clone();
        surveyAssignmentsDaoConfig.initIdentityScope(type);

        surveyQuestionsDaoConfig = daoConfigMap.get(SurveyQuestionsDao.class).clone();
        surveyQuestionsDaoConfig.initIdentityScope(type);

        subQuestionAnswersDaoConfig = daoConfigMap.get(SubQuestionAnswersDao.class).clone();
        subQuestionAnswersDaoConfig.initIdentityScope(type);

        movementDetailsDaoConfig = daoConfigMap.get(MovementDetailsDao.class).clone();
        movementDetailsDaoConfig.initIdentityScope(type);

        promotionAssignmentsDaoConfig = daoConfigMap.get(PromotionAssignmentsDao.class).clone();
        promotionAssignmentsDaoConfig.initIdentityScope(type);

        vehiclesDaoConfig = daoConfigMap.get(VehiclesDao.class).clone();
        vehiclesDaoConfig.initIdentityScope(type);

        deviceTrackingDaoConfig = daoConfigMap.get(DeviceTrackingDao.class).clone();
        deviceTrackingDaoConfig.initIdentityScope(type);

        vehiclesStockDaoConfig = daoConfigMap.get(VehiclesStockDao.class).clone();
        vehiclesStockDaoConfig.initIdentityScope(type);

        wareHouseBarcodesDaoConfig = daoConfigMap.get(WareHouseBarcodesDao.class).clone();
        wareHouseBarcodesDaoConfig.initIdentityScope(type);

        transactionSequencesDaoConfig = daoConfigMap.get(TransactionSequencesDao.class).clone();
        transactionSequencesDaoConfig.initIdentityScope(type);

        surveyAnswersDaoConfig = daoConfigMap.get(SurveyAnswersDao.class).clone();
        surveyAnswersDaoConfig.initIdentityScope(type);

        promotionDetailsDaoConfig = daoConfigMap.get(PromotionDetailsDao.class).clone();
        promotionDetailsDaoConfig.initIdentityScope(type);

        questionAnswersDaoConfig = daoConfigMap.get(QuestionAnswersDao.class).clone();
        questionAnswersDaoConfig.initIdentityScope(type);

        movementHeadersDaoConfig = daoConfigMap.get(MovementHeadersDao.class).clone();
        movementHeadersDaoConfig.initIdentityScope(type);

        surveyImagesDaoConfig = daoConfigMap.get(SurveyImagesDao.class).clone();
        surveyImagesDaoConfig.initIdentityScope(type);

        deviceSerialsDaoConfig = daoConfigMap.get(DeviceSerialsDao.class).clone();
        deviceSerialsDaoConfig.initIdentityScope(type);

        surveysDaoConfig = daoConfigMap.get(SurveysDao.class).clone();
        surveysDaoConfig.initIdentityScope(type);

        transactionDetailsDaoConfig = daoConfigMap.get(TransactionDetailsDao.class).clone();
        transactionDetailsDaoConfig.initIdentityScope(type);

        clientUserCollectionDaoConfig = daoConfigMap.get(ClientUserCollectionDao.class).clone();
        clientUserCollectionDaoConfig.initIdentityScope(type);

        collectionInvoicesDaoConfig = daoConfigMap.get(CollectionInvoicesDao.class).clone();
        collectionInvoicesDaoConfig.initIdentityScope(type);

        totalClientDuesDaoConfig = daoConfigMap.get(TotalClientDuesDao.class).clone();
        totalClientDuesDaoConfig.initIdentityScope(type);

        warehouseQuantitiesDaoConfig = daoConfigMap.get(WarehouseQuantitiesDao.class).clone();
        warehouseQuantitiesDaoConfig.initIdentityScope(type);

        moneyCategoryDaoConfig = daoConfigMap.get(MoneyCategoryDao.class).clone();
        moneyCategoryDaoConfig.initIdentityScope(type);

        collectionDetailsDaoConfig = daoConfigMap.get(CollectionDetailsDao.class).clone();
        collectionDetailsDaoConfig.initIdentityScope(type);

        collectionImagesDaoConfig = daoConfigMap.get(CollectionImagesDao.class).clone();
        collectionImagesDaoConfig.initIdentityScope(type);

        clientDuesDaoConfig = daoConfigMap.get(ClientDuesDao.class).clone();
        clientDuesDaoConfig.initIdentityScope(type);

        collectionHeadersDaoConfig = daoConfigMap.get(CollectionHeadersDao.class).clone();
        collectionHeadersDaoConfig.initIdentityScope(type);

        brandsDaoConfig = daoConfigMap.get(BrandsDao.class).clone();
        brandsDaoConfig.initIdentityScope(type);

        clientAssetsPicturesDaoConfig = daoConfigMap.get(ClientAssetsPicturesDao.class).clone();
        clientAssetsPicturesDaoConfig.initIdentityScope(type);

        shareOfShelfTrackerDaoConfig = daoConfigMap.get(ShareOfShelfTrackerDao.class).clone();
        shareOfShelfTrackerDaoConfig.initIdentityScope(type);

        sOSTrackerHeadersDaoConfig = daoConfigMap.get(SOSTrackerHeadersDao.class).clone();
        sOSTrackerHeadersDaoConfig.initIdentityScope(type);

        userVersionsDaoConfig = daoConfigMap.get(UserVersionsDao.class).clone();
        userVersionsDaoConfig.initIdentityScope(type);

        userSpecialPriceListsDaoConfig = daoConfigMap.get(UserSpecialPriceListsDao.class).clone();
        userSpecialPriceListsDaoConfig.initIdentityScope(type);

        forceSyncJourneysDaoConfig = daoConfigMap.get(ForceSyncJourneysDao.class).clone();
        forceSyncJourneysDaoConfig.initIdentityScope(type);

        newSkuListDivisionsDaoConfig = daoConfigMap.get(NewSkuListDivisionsDao.class).clone();
        newSkuListDivisionsDaoConfig.initIdentityScope(type);

        sOSTargetAssignmentDaoConfig = daoConfigMap.get(SOSTargetAssignmentDao.class).clone();
        sOSTargetAssignmentDaoConfig.initIdentityScope(type);

        clientMouvementStockDaoConfig = daoConfigMap.get(ClientMouvementStockDao.class).clone();
        clientMouvementStockDaoConfig.initIdentityScope(type);

        sOSTrackerDetailsDaoConfig = daoConfigMap.get(SOSTrackerDetailsDao.class).clone();
        sOSTrackerDetailsDaoConfig.initIdentityScope(type);

        sOSTargetHeadersDaoConfig = daoConfigMap.get(SOSTargetHeadersDao.class).clone();
        sOSTargetHeadersDaoConfig.initIdentityScope(type);

        duoUsersDaoConfig = daoConfigMap.get(DuoUsersDao.class).clone();
        duoUsersDaoConfig.initIdentityScope(type);

        clientNewSkuListDaoConfig = daoConfigMap.get(ClientNewSkuListDao.class).clone();
        clientNewSkuListDaoConfig.initIdentityScope(type);

        objectivesDaoConfig = daoConfigMap.get(ObjectivesDao.class).clone();
        objectivesDaoConfig.initIdentityScope(type);

        sOSBrandsDaoConfig = daoConfigMap.get(SOSBrandsDao.class).clone();
        sOSBrandsDaoConfig.initIdentityScope(type);

        sOSCategoryBrandsDaoConfig = daoConfigMap.get(SOSCategoryBrandsDao.class).clone();
        sOSCategoryBrandsDaoConfig.initIdentityScope(type);

        mSLDivisionsDaoConfig = daoConfigMap.get(MSLDivisionsDao.class).clone();
        mSLDivisionsDaoConfig.initIdentityScope(type);

        shareOfShelvesDaoConfig = daoConfigMap.get(ShareOfShelvesDao.class).clone();
        shareOfShelvesDaoConfig.initIdentityScope(type);

        sOSCategoriesDaoConfig = daoConfigMap.get(SOSCategoriesDao.class).clone();
        sOSCategoriesDaoConfig.initIdentityScope(type);

        sOSTargetDetailsDaoConfig = daoConfigMap.get(SOSTargetDetailsDao.class).clone();
        sOSTargetDetailsDaoConfig.initIdentityScope(type);

        clientAssetStatusDaoConfig = daoConfigMap.get(ClientAssetStatusDao.class).clone();
        clientAssetStatusDaoConfig.initIdentityScope(type);

        clientCreditingsDaoConfig = daoConfigMap.get(ClientCreditingsDao.class).clone();
        clientCreditingsDaoConfig.initIdentityScope(type);

        targetAssignmentsDaoConfig = daoConfigMap.get(TargetAssignmentsDao.class).clone();
        targetAssignmentsDaoConfig.initIdentityScope(type);

        objectivePrioritiesDaoConfig = daoConfigMap.get(ObjectivePrioritiesDao.class).clone();
        objectivePrioritiesDaoConfig.initIdentityScope(type);

        targetHeadersDaoConfig = daoConfigMap.get(TargetHeadersDao.class).clone();
        targetHeadersDaoConfig.initIdentityScope(type);

        targetDetailsDaoConfig = daoConfigMap.get(TargetDetailsDao.class).clone();
        targetDetailsDaoConfig.initIdentityScope(type);

        objectiveAchievementsDaoConfig = daoConfigMap.get(ObjectiveAchievementsDao.class).clone();
        objectiveAchievementsDaoConfig.initIdentityScope(type);

        targetAchievementsDaoConfig = daoConfigMap.get(TargetAchievementsDao.class).clone();
        targetAchievementsDaoConfig.initIdentityScope(type);

        banksDao = new BanksDao(banksDaoConfig, this);
        areasDao = new AreasDao(areasDaoConfig, this);
        usersDao = new UsersDao(usersDaoConfig, this);
        connectionSettingsDao = new ConnectionSettingsDao(connectionSettingsDaoConfig, this);
        areaLevelsDao = new AreaLevelsDao(areaLevelsDaoConfig, this);
        companiesDao = new CompaniesDao(companiesDaoConfig, this);
        permissionsDao = new PermissionsDao(permissionsDaoConfig, this);
        newClientsDao = new NewClientsDao(newClientsDaoConfig, this);
        newClientImagesDao = new NewClientImagesDao(newClientImagesDaoConfig, this);
        clientsDao = new ClientsDao(clientsDaoConfig, this);
        userPasswordsDao = new UserPasswordsDao(userPasswordsDaoConfig, this);
        msgHeaderDao = new MsgHeaderDao(msgHeaderDaoConfig, this);
        userPermissionsDao = new UserPermissionsDao(userPermissionsDaoConfig, this);
        userAccountNumbersDao = new UserAccountNumbersDao(userAccountNumbersDaoConfig, this);
        printersDao = new PrintersDao(printersDaoConfig, this);
        userLimitsDao = new UserLimitsDao(userLimitsDaoConfig, this);
        msgNoteDao = new MsgNoteDao(msgNoteDaoConfig, this);
        itemsDao = new ItemsDao(itemsDaoConfig, this);
        cyclesDao = new CyclesDao(cyclesDaoConfig, this);
        taxesDao = new TaxesDao(taxesDaoConfig, this);
        unitsDao = new UnitsDao(unitsDaoConfig, this);
        pricesDao = new PricesDao(pricesDaoConfig, this);
        clientBarcodesDao = new ClientBarcodesDao(clientBarcodesDaoConfig, this);
        divisionsDao = new DivisionsDao(divisionsDaoConfig, this);
        userDivisionsDao = new UserDivisionsDao(userDivisionsDaoConfig, this);
        itemDivisionsDao = new ItemDivisionsDao(itemDivisionsDaoConfig, this);
        itemVolumesDao = new ItemVolumesDao(itemVolumesDaoConfig, this);
        clientAreasDao = new ClientAreasDao(clientAreasDaoConfig, this);
        clientItemCodesDao = new ClientItemCodesDao(clientItemCodesDaoConfig, this);
        clientContactsDao = new ClientContactsDao(clientContactsDaoConfig, this);
        clientPropertyValuesDao = new ClientPropertyValuesDao(clientPropertyValuesDaoConfig, this);
        userCyclesDao = new UserCyclesDao(userCyclesDaoConfig, this);
        itemBarcodesDao = new ItemBarcodesDao(itemBarcodesDaoConfig, this);
        clientPropertiesDao = new ClientPropertiesDao(clientPropertiesDaoConfig, this);
        clientPriceListsDao = new ClientPriceListsDao(clientPriceListsDaoConfig, this);
        priceListsDao = new PriceListsDao(priceListsDaoConfig, this);
        objectiveAssignmentsDao = new ObjectiveAssignmentsDao(objectiveAssignmentsDaoConfig, this);
        cycleCallsDao = new CycleCallsDao(cycleCallsDaoConfig, this);
        clientCurrenciesDao = new ClientCurrenciesDao(clientCurrenciesDaoConfig, this);
        clientPropertyLevelsDao = new ClientPropertyLevelsDao(clientPropertyLevelsDaoConfig, this);
        clientItemHistoryDao = new ClientItemHistoryDao(clientItemHistoryDaoConfig, this);
        assetsStatusDao = new AssetsStatusDao(assetsStatusDaoConfig, this);
        clientDivisionsDao = new ClientDivisionsDao(clientDivisionsDaoConfig, this);
        clientAvailabilitiesDao = new ClientAvailabilitiesDao(clientAvailabilitiesDaoConfig, this);
        clientMustStockListDao = new ClientMustStockListDao(clientMustStockListDaoConfig, this);
        currenciesDao = new CurrenciesDao(currenciesDaoConfig, this);
        competitorItemsDao = new CompetitorItemsDao(competitorItemsDaoConfig, this);
        paymentTermsDao = new PaymentTermsDao(paymentTermsDaoConfig, this);
        blankVisitsDao = new BlankVisitsDao(blankVisitsDaoConfig, this);
        journeysDao = new JourneysDao(journeysDaoConfig, this);
        blankVisitContactsDao = new BlankVisitContactsDao(blankVisitContactsDaoConfig, this);
        visitHistoryDao = new VisitHistoryDao(visitHistoryDaoConfig, this);
        visitReasonsDao = new VisitReasonsDao(visitReasonsDaoConfig, this);
        reasonsDao = new ReasonsDao(reasonsDaoConfig, this);
        visitTypesDao = new VisitTypesDao(visitTypesDaoConfig, this);
        transactionHeadersDao = new TransactionHeadersDao(transactionHeadersDaoConfig, this);
        clientDocumentImagesDao = new ClientDocumentImagesDao(clientDocumentImagesDaoConfig, this);
        clientStockCountDetailsDao = new ClientStockCountDetailsDao(clientStockCountDetailsDaoConfig, this);
        clientItemClassificationHistoryDao = new ClientItemClassificationHistoryDao(clientItemClassificationHistoryDaoConfig, this);
        wareHouseBarcodeScanningDao = new WareHouseBarcodeScanningDao(wareHouseBarcodeScanningDaoConfig, this);
        clientSellingSuggestionDao = new ClientSellingSuggestionDao(clientSellingSuggestionDaoConfig, this);
        transactionPromotionDetailsDao = new TransactionPromotionDetailsDao(transactionPromotionDetailsDaoConfig, this);
        transactionDetailsMissedMSLDao = new TransactionDetailsMissedMSLDao(transactionDetailsMissedMSLDaoConfig, this);
        clientStockCountHeadersDao = new ClientStockCountHeadersDao(clientStockCountHeadersDaoConfig, this);
        dailyTargetAchievementsDao = new DailyTargetAchievementsDao(dailyTargetAchievementsDaoConfig, this);
        clientItemClassificationLevelsDao = new ClientItemClassificationLevelsDao(clientItemClassificationLevelsDaoConfig, this);
        clientItemClassificationsDao = new ClientItemClassificationsDao(clientItemClassificationsDaoConfig, this);
        visitsDao = new VisitsDao(visitsDaoConfig, this);
        suggestedUserLoadDao = new SuggestedUserLoadDao(suggestedUserLoadDaoConfig, this);
        promotionHeadersDao = new PromotionHeadersDao(promotionHeadersDaoConfig, this);
        surveyAssignmentsDao = new SurveyAssignmentsDao(surveyAssignmentsDaoConfig, this);
        surveyQuestionsDao = new SurveyQuestionsDao(surveyQuestionsDaoConfig, this);
        subQuestionAnswersDao = new SubQuestionAnswersDao(subQuestionAnswersDaoConfig, this);
        movementDetailsDao = new MovementDetailsDao(movementDetailsDaoConfig, this);
        promotionAssignmentsDao = new PromotionAssignmentsDao(promotionAssignmentsDaoConfig, this);
        vehiclesDao = new VehiclesDao(vehiclesDaoConfig, this);
        deviceTrackingDao = new DeviceTrackingDao(deviceTrackingDaoConfig, this);
        vehiclesStockDao = new VehiclesStockDao(vehiclesStockDaoConfig, this);
        wareHouseBarcodesDao = new WareHouseBarcodesDao(wareHouseBarcodesDaoConfig, this);
        transactionSequencesDao = new TransactionSequencesDao(transactionSequencesDaoConfig, this);
        surveyAnswersDao = new SurveyAnswersDao(surveyAnswersDaoConfig, this);
        promotionDetailsDao = new PromotionDetailsDao(promotionDetailsDaoConfig, this);
        questionAnswersDao = new QuestionAnswersDao(questionAnswersDaoConfig, this);
        movementHeadersDao = new MovementHeadersDao(movementHeadersDaoConfig, this);
        surveyImagesDao = new SurveyImagesDao(surveyImagesDaoConfig, this);
        deviceSerialsDao = new DeviceSerialsDao(deviceSerialsDaoConfig, this);
        surveysDao = new SurveysDao(surveysDaoConfig, this);
        transactionDetailsDao = new TransactionDetailsDao(transactionDetailsDaoConfig, this);
        clientUserCollectionDao = new ClientUserCollectionDao(clientUserCollectionDaoConfig, this);
        collectionInvoicesDao = new CollectionInvoicesDao(collectionInvoicesDaoConfig, this);
        totalClientDuesDao = new TotalClientDuesDao(totalClientDuesDaoConfig, this);
        warehouseQuantitiesDao = new WarehouseQuantitiesDao(warehouseQuantitiesDaoConfig, this);
        moneyCategoryDao = new MoneyCategoryDao(moneyCategoryDaoConfig, this);
        collectionDetailsDao = new CollectionDetailsDao(collectionDetailsDaoConfig, this);
        collectionImagesDao = new CollectionImagesDao(collectionImagesDaoConfig, this);
        clientDuesDao = new ClientDuesDao(clientDuesDaoConfig, this);
        collectionHeadersDao = new CollectionHeadersDao(collectionHeadersDaoConfig, this);
        brandsDao = new BrandsDao(brandsDaoConfig, this);
        clientAssetsPicturesDao = new ClientAssetsPicturesDao(clientAssetsPicturesDaoConfig, this);
        shareOfShelfTrackerDao = new ShareOfShelfTrackerDao(shareOfShelfTrackerDaoConfig, this);
        sOSTrackerHeadersDao = new SOSTrackerHeadersDao(sOSTrackerHeadersDaoConfig, this);
        userVersionsDao = new UserVersionsDao(userVersionsDaoConfig, this);
        userSpecialPriceListsDao = new UserSpecialPriceListsDao(userSpecialPriceListsDaoConfig, this);
        forceSyncJourneysDao = new ForceSyncJourneysDao(forceSyncJourneysDaoConfig, this);
        newSkuListDivisionsDao = new NewSkuListDivisionsDao(newSkuListDivisionsDaoConfig, this);
        sOSTargetAssignmentDao = new SOSTargetAssignmentDao(sOSTargetAssignmentDaoConfig, this);
        clientMouvementStockDao = new ClientMouvementStockDao(clientMouvementStockDaoConfig, this);
        sOSTrackerDetailsDao = new SOSTrackerDetailsDao(sOSTrackerDetailsDaoConfig, this);
        sOSTargetHeadersDao = new SOSTargetHeadersDao(sOSTargetHeadersDaoConfig, this);
        duoUsersDao = new DuoUsersDao(duoUsersDaoConfig, this);
        clientNewSkuListDao = new ClientNewSkuListDao(clientNewSkuListDaoConfig, this);
        objectivesDao = new ObjectivesDao(objectivesDaoConfig, this);
        sOSBrandsDao = new SOSBrandsDao(sOSBrandsDaoConfig, this);
        sOSCategoryBrandsDao = new SOSCategoryBrandsDao(sOSCategoryBrandsDaoConfig, this);
        mSLDivisionsDao = new MSLDivisionsDao(mSLDivisionsDaoConfig, this);
        shareOfShelvesDao = new ShareOfShelvesDao(shareOfShelvesDaoConfig, this);
        sOSCategoriesDao = new SOSCategoriesDao(sOSCategoriesDaoConfig, this);
        sOSTargetDetailsDao = new SOSTargetDetailsDao(sOSTargetDetailsDaoConfig, this);
        clientAssetStatusDao = new ClientAssetStatusDao(clientAssetStatusDaoConfig, this);
        clientCreditingsDao = new ClientCreditingsDao(clientCreditingsDaoConfig, this);
        targetAssignmentsDao = new TargetAssignmentsDao(targetAssignmentsDaoConfig, this);
        objectivePrioritiesDao = new ObjectivePrioritiesDao(objectivePrioritiesDaoConfig, this);
        targetHeadersDao = new TargetHeadersDao(targetHeadersDaoConfig, this);
        targetDetailsDao = new TargetDetailsDao(targetDetailsDaoConfig, this);
        objectiveAchievementsDao = new ObjectiveAchievementsDao(objectiveAchievementsDaoConfig, this);
        targetAchievementsDao = new TargetAchievementsDao(targetAchievementsDaoConfig, this);

        registerDao(Banks.class, banksDao);
        registerDao(Areas.class, areasDao);
        registerDao(Users.class, usersDao);
        registerDao(ConnectionSettings.class, connectionSettingsDao);
        registerDao(AreaLevels.class, areaLevelsDao);
        registerDao(Companies.class, companiesDao);
        registerDao(Permissions.class, permissionsDao);
        registerDao(NewClients.class, newClientsDao);
        registerDao(NewClientImages.class, newClientImagesDao);
        registerDao(Clients.class, clientsDao);
        registerDao(UserPasswords.class, userPasswordsDao);
        registerDao(MsgHeader.class, msgHeaderDao);
        registerDao(UserPermissions.class, userPermissionsDao);
        registerDao(UserAccountNumbers.class, userAccountNumbersDao);
        registerDao(Printers.class, printersDao);
        registerDao(UserLimits.class, userLimitsDao);
        registerDao(MsgNote.class, msgNoteDao);
        registerDao(Items.class, itemsDao);
        registerDao(Cycles.class, cyclesDao);
        registerDao(Taxes.class, taxesDao);
        registerDao(Units.class, unitsDao);
        registerDao(Prices.class, pricesDao);
        registerDao(ClientBarcodes.class, clientBarcodesDao);
        registerDao(Divisions.class, divisionsDao);
        registerDao(UserDivisions.class, userDivisionsDao);
        registerDao(ItemDivisions.class, itemDivisionsDao);
        registerDao(ItemVolumes.class, itemVolumesDao);
        registerDao(ClientAreas.class, clientAreasDao);
        registerDao(ClientItemCodes.class, clientItemCodesDao);
        registerDao(ClientContacts.class, clientContactsDao);
        registerDao(ClientPropertyValues.class, clientPropertyValuesDao);
        registerDao(UserCycles.class, userCyclesDao);
        registerDao(ItemBarcodes.class, itemBarcodesDao);
        registerDao(ClientProperties.class, clientPropertiesDao);
        registerDao(ClientPriceLists.class, clientPriceListsDao);
        registerDao(PriceLists.class, priceListsDao);
        registerDao(ObjectiveAssignments.class, objectiveAssignmentsDao);
        registerDao(CycleCalls.class, cycleCallsDao);
        registerDao(ClientCurrencies.class, clientCurrenciesDao);
        registerDao(ClientPropertyLevels.class, clientPropertyLevelsDao);
        registerDao(ClientItemHistory.class, clientItemHistoryDao);
        registerDao(AssetsStatus.class, assetsStatusDao);
        registerDao(ClientDivisions.class, clientDivisionsDao);
        registerDao(ClientAvailabilities.class, clientAvailabilitiesDao);
        registerDao(ClientMustStockList.class, clientMustStockListDao);
        registerDao(Currencies.class, currenciesDao);
        registerDao(CompetitorItems.class, competitorItemsDao);
        registerDao(PaymentTerms.class, paymentTermsDao);
        registerDao(BlankVisits.class, blankVisitsDao);
        registerDao(Journeys.class, journeysDao);
        registerDao(BlankVisitContacts.class, blankVisitContactsDao);
        registerDao(VisitHistory.class, visitHistoryDao);
        registerDao(VisitReasons.class, visitReasonsDao);
        registerDao(Reasons.class, reasonsDao);
        registerDao(VisitTypes.class, visitTypesDao);
        registerDao(TransactionHeaders.class, transactionHeadersDao);
        registerDao(ClientDocumentImages.class, clientDocumentImagesDao);
        registerDao(ClientStockCountDetails.class, clientStockCountDetailsDao);
        registerDao(ClientItemClassificationHistory.class, clientItemClassificationHistoryDao);
        registerDao(WareHouseBarcodeScanning.class, wareHouseBarcodeScanningDao);
        registerDao(ClientSellingSuggestion.class, clientSellingSuggestionDao);
        registerDao(TransactionPromotionDetails.class, transactionPromotionDetailsDao);
        registerDao(TransactionDetailsMissedMSL.class, transactionDetailsMissedMSLDao);
        registerDao(ClientStockCountHeaders.class, clientStockCountHeadersDao);
        registerDao(DailyTargetAchievements.class, dailyTargetAchievementsDao);
        registerDao(ClientItemClassificationLevels.class, clientItemClassificationLevelsDao);
        registerDao(ClientItemClassifications.class, clientItemClassificationsDao);
        registerDao(Visits.class, visitsDao);
        registerDao(SuggestedUserLoad.class, suggestedUserLoadDao);
        registerDao(PromotionHeaders.class, promotionHeadersDao);
        registerDao(SurveyAssignments.class, surveyAssignmentsDao);
        registerDao(SurveyQuestions.class, surveyQuestionsDao);
        registerDao(SubQuestionAnswers.class, subQuestionAnswersDao);
        registerDao(MovementDetails.class, movementDetailsDao);
        registerDao(PromotionAssignments.class, promotionAssignmentsDao);
        registerDao(Vehicles.class, vehiclesDao);
        registerDao(DeviceTracking.class, deviceTrackingDao);
        registerDao(VehiclesStock.class, vehiclesStockDao);
        registerDao(WareHouseBarcodes.class, wareHouseBarcodesDao);
        registerDao(TransactionSequences.class, transactionSequencesDao);
        registerDao(SurveyAnswers.class, surveyAnswersDao);
        registerDao(PromotionDetails.class, promotionDetailsDao);
        registerDao(QuestionAnswers.class, questionAnswersDao);
        registerDao(MovementHeaders.class, movementHeadersDao);
        registerDao(SurveyImages.class, surveyImagesDao);
        registerDao(DeviceSerials.class, deviceSerialsDao);
        registerDao(Surveys.class, surveysDao);
        registerDao(TransactionDetails.class, transactionDetailsDao);
        registerDao(ClientUserCollection.class, clientUserCollectionDao);
        registerDao(CollectionInvoices.class, collectionInvoicesDao);
        registerDao(TotalClientDues.class, totalClientDuesDao);
        registerDao(WarehouseQuantities.class, warehouseQuantitiesDao);
        registerDao(MoneyCategory.class, moneyCategoryDao);
        registerDao(CollectionDetails.class, collectionDetailsDao);
        registerDao(CollectionImages.class, collectionImagesDao);
        registerDao(ClientDues.class, clientDuesDao);
        registerDao(CollectionHeaders.class, collectionHeadersDao);
        registerDao(Brands.class, brandsDao);
        registerDao(ClientAssetsPictures.class, clientAssetsPicturesDao);
        registerDao(ShareOfShelfTracker.class, shareOfShelfTrackerDao);
        registerDao(SOSTrackerHeaders.class, sOSTrackerHeadersDao);
        registerDao(UserVersions.class, userVersionsDao);
        registerDao(UserSpecialPriceLists.class, userSpecialPriceListsDao);
        registerDao(ForceSyncJourneys.class, forceSyncJourneysDao);
        registerDao(NewSkuListDivisions.class, newSkuListDivisionsDao);
        registerDao(SOSTargetAssignment.class, sOSTargetAssignmentDao);
        registerDao(ClientMouvementStock.class, clientMouvementStockDao);
        registerDao(SOSTrackerDetails.class, sOSTrackerDetailsDao);
        registerDao(SOSTargetHeaders.class, sOSTargetHeadersDao);
        registerDao(DuoUsers.class, duoUsersDao);
        registerDao(ClientNewSkuList.class, clientNewSkuListDao);
        registerDao(Objectives.class, objectivesDao);
        registerDao(SOSBrands.class, sOSBrandsDao);
        registerDao(SOSCategoryBrands.class, sOSCategoryBrandsDao);
        registerDao(MSLDivisions.class, mSLDivisionsDao);
        registerDao(ShareOfShelves.class, shareOfShelvesDao);
        registerDao(SOSCategories.class, sOSCategoriesDao);
        registerDao(SOSTargetDetails.class, sOSTargetDetailsDao);
        registerDao(ClientAssetStatus.class, clientAssetStatusDao);
        registerDao(ClientCreditings.class, clientCreditingsDao);
        registerDao(TargetAssignments.class, targetAssignmentsDao);
        registerDao(ObjectivePriorities.class, objectivePrioritiesDao);
        registerDao(TargetHeaders.class, targetHeadersDao);
        registerDao(TargetDetails.class, targetDetailsDao);
        registerDao(ObjectiveAchievements.class, objectiveAchievementsDao);
        registerDao(TargetAchievements.class, targetAchievementsDao);
    }
    
    public void clear() {
        banksDaoConfig.getIdentityScope().clear();
        areasDaoConfig.getIdentityScope().clear();
        usersDaoConfig.getIdentityScope().clear();
        connectionSettingsDaoConfig.getIdentityScope().clear();
        areaLevelsDaoConfig.getIdentityScope().clear();
        companiesDaoConfig.getIdentityScope().clear();
        permissionsDaoConfig.getIdentityScope().clear();
        newClientsDaoConfig.getIdentityScope().clear();
        newClientImagesDaoConfig.getIdentityScope().clear();
        clientsDaoConfig.getIdentityScope().clear();
        userPasswordsDaoConfig.getIdentityScope().clear();
        msgHeaderDaoConfig.getIdentityScope().clear();
        userPermissionsDaoConfig.getIdentityScope().clear();
        userAccountNumbersDaoConfig.getIdentityScope().clear();
        printersDaoConfig.getIdentityScope().clear();
        userLimitsDaoConfig.getIdentityScope().clear();
        msgNoteDaoConfig.getIdentityScope().clear();
        itemsDaoConfig.getIdentityScope().clear();
        cyclesDaoConfig.getIdentityScope().clear();
        taxesDaoConfig.getIdentityScope().clear();
        unitsDaoConfig.getIdentityScope().clear();
        pricesDaoConfig.getIdentityScope().clear();
        clientBarcodesDaoConfig.getIdentityScope().clear();
        divisionsDaoConfig.getIdentityScope().clear();
        userDivisionsDaoConfig.getIdentityScope().clear();
        itemDivisionsDaoConfig.getIdentityScope().clear();
        itemVolumesDaoConfig.getIdentityScope().clear();
        clientAreasDaoConfig.getIdentityScope().clear();
        clientItemCodesDaoConfig.getIdentityScope().clear();
        clientContactsDaoConfig.getIdentityScope().clear();
        clientPropertyValuesDaoConfig.getIdentityScope().clear();
        userCyclesDaoConfig.getIdentityScope().clear();
        itemBarcodesDaoConfig.getIdentityScope().clear();
        clientPropertiesDaoConfig.getIdentityScope().clear();
        clientPriceListsDaoConfig.getIdentityScope().clear();
        priceListsDaoConfig.getIdentityScope().clear();
        objectiveAssignmentsDaoConfig.getIdentityScope().clear();
        cycleCallsDaoConfig.getIdentityScope().clear();
        clientCurrenciesDaoConfig.getIdentityScope().clear();
        clientPropertyLevelsDaoConfig.getIdentityScope().clear();
        clientItemHistoryDaoConfig.getIdentityScope().clear();
        assetsStatusDaoConfig.getIdentityScope().clear();
        clientDivisionsDaoConfig.getIdentityScope().clear();
        clientAvailabilitiesDaoConfig.getIdentityScope().clear();
        clientMustStockListDaoConfig.getIdentityScope().clear();
        currenciesDaoConfig.getIdentityScope().clear();
        competitorItemsDaoConfig.getIdentityScope().clear();
        paymentTermsDaoConfig.getIdentityScope().clear();
        blankVisitsDaoConfig.getIdentityScope().clear();
        journeysDaoConfig.getIdentityScope().clear();
        blankVisitContactsDaoConfig.getIdentityScope().clear();
        visitHistoryDaoConfig.getIdentityScope().clear();
        visitReasonsDaoConfig.getIdentityScope().clear();
        reasonsDaoConfig.getIdentityScope().clear();
        visitTypesDaoConfig.getIdentityScope().clear();
        transactionHeadersDaoConfig.getIdentityScope().clear();
        clientDocumentImagesDaoConfig.getIdentityScope().clear();
        clientStockCountDetailsDaoConfig.getIdentityScope().clear();
        clientItemClassificationHistoryDaoConfig.getIdentityScope().clear();
        wareHouseBarcodeScanningDaoConfig.getIdentityScope().clear();
        clientSellingSuggestionDaoConfig.getIdentityScope().clear();
        transactionPromotionDetailsDaoConfig.getIdentityScope().clear();
        transactionDetailsMissedMSLDaoConfig.getIdentityScope().clear();
        clientStockCountHeadersDaoConfig.getIdentityScope().clear();
        dailyTargetAchievementsDaoConfig.getIdentityScope().clear();
        clientItemClassificationLevelsDaoConfig.getIdentityScope().clear();
        clientItemClassificationsDaoConfig.getIdentityScope().clear();
        visitsDaoConfig.getIdentityScope().clear();
        suggestedUserLoadDaoConfig.getIdentityScope().clear();
        promotionHeadersDaoConfig.getIdentityScope().clear();
        surveyAssignmentsDaoConfig.getIdentityScope().clear();
        surveyQuestionsDaoConfig.getIdentityScope().clear();
        subQuestionAnswersDaoConfig.getIdentityScope().clear();
        movementDetailsDaoConfig.getIdentityScope().clear();
        promotionAssignmentsDaoConfig.getIdentityScope().clear();
        vehiclesDaoConfig.getIdentityScope().clear();
        deviceTrackingDaoConfig.getIdentityScope().clear();
        vehiclesStockDaoConfig.getIdentityScope().clear();
        wareHouseBarcodesDaoConfig.getIdentityScope().clear();
        transactionSequencesDaoConfig.getIdentityScope().clear();
        surveyAnswersDaoConfig.getIdentityScope().clear();
        promotionDetailsDaoConfig.getIdentityScope().clear();
        questionAnswersDaoConfig.getIdentityScope().clear();
        movementHeadersDaoConfig.getIdentityScope().clear();
        surveyImagesDaoConfig.getIdentityScope().clear();
        deviceSerialsDaoConfig.getIdentityScope().clear();
        surveysDaoConfig.getIdentityScope().clear();
        transactionDetailsDaoConfig.getIdentityScope().clear();
        clientUserCollectionDaoConfig.getIdentityScope().clear();
        collectionInvoicesDaoConfig.getIdentityScope().clear();
        totalClientDuesDaoConfig.getIdentityScope().clear();
        warehouseQuantitiesDaoConfig.getIdentityScope().clear();
        moneyCategoryDaoConfig.getIdentityScope().clear();
        collectionDetailsDaoConfig.getIdentityScope().clear();
        collectionImagesDaoConfig.getIdentityScope().clear();
        clientDuesDaoConfig.getIdentityScope().clear();
        collectionHeadersDaoConfig.getIdentityScope().clear();
        brandsDaoConfig.getIdentityScope().clear();
        clientAssetsPicturesDaoConfig.getIdentityScope().clear();
        shareOfShelfTrackerDaoConfig.getIdentityScope().clear();
        sOSTrackerHeadersDaoConfig.getIdentityScope().clear();
        userVersionsDaoConfig.getIdentityScope().clear();
        userSpecialPriceListsDaoConfig.getIdentityScope().clear();
        forceSyncJourneysDaoConfig.getIdentityScope().clear();
        newSkuListDivisionsDaoConfig.getIdentityScope().clear();
        sOSTargetAssignmentDaoConfig.getIdentityScope().clear();
        clientMouvementStockDaoConfig.getIdentityScope().clear();
        sOSTrackerDetailsDaoConfig.getIdentityScope().clear();
        sOSTargetHeadersDaoConfig.getIdentityScope().clear();
        duoUsersDaoConfig.getIdentityScope().clear();
        clientNewSkuListDaoConfig.getIdentityScope().clear();
        objectivesDaoConfig.getIdentityScope().clear();
        sOSBrandsDaoConfig.getIdentityScope().clear();
        sOSCategoryBrandsDaoConfig.getIdentityScope().clear();
        mSLDivisionsDaoConfig.getIdentityScope().clear();
        shareOfShelvesDaoConfig.getIdentityScope().clear();
        sOSCategoriesDaoConfig.getIdentityScope().clear();
        sOSTargetDetailsDaoConfig.getIdentityScope().clear();
        clientAssetStatusDaoConfig.getIdentityScope().clear();
        clientCreditingsDaoConfig.getIdentityScope().clear();
        targetAssignmentsDaoConfig.getIdentityScope().clear();
        objectivePrioritiesDaoConfig.getIdentityScope().clear();
        targetHeadersDaoConfig.getIdentityScope().clear();
        targetDetailsDaoConfig.getIdentityScope().clear();
        objectiveAchievementsDaoConfig.getIdentityScope().clear();
        targetAchievementsDaoConfig.getIdentityScope().clear();
    }

    public BanksDao getBanksDao() {
        return banksDao;
    }

    public AreasDao getAreasDao() {
        return areasDao;
    }

    public UsersDao getUsersDao() {
        return usersDao;
    }

    public ConnectionSettingsDao getConnectionSettingsDao() {
        return connectionSettingsDao;
    }

    public AreaLevelsDao getAreaLevelsDao() {
        return areaLevelsDao;
    }

    public CompaniesDao getCompaniesDao() {
        return companiesDao;
    }

    public PermissionsDao getPermissionsDao() {
        return permissionsDao;
    }

    public NewClientsDao getNewClientsDao() {
        return newClientsDao;
    }

    public NewClientImagesDao getNewClientImagesDao() {
        return newClientImagesDao;
    }

    public ClientsDao getClientsDao() {
        return clientsDao;
    }

    public UserPasswordsDao getUserPasswordsDao() {
        return userPasswordsDao;
    }

    public MsgHeaderDao getMsgHeaderDao() {
        return msgHeaderDao;
    }

    public UserPermissionsDao getUserPermissionsDao() {
        return userPermissionsDao;
    }

    public UserAccountNumbersDao getUserAccountNumbersDao() {
        return userAccountNumbersDao;
    }

    public PrintersDao getPrintersDao() {
        return printersDao;
    }

    public UserLimitsDao getUserLimitsDao() {
        return userLimitsDao;
    }

    public MsgNoteDao getMsgNoteDao() {
        return msgNoteDao;
    }

    public ItemsDao getItemsDao() {
        return itemsDao;
    }

    public CyclesDao getCyclesDao() {
        return cyclesDao;
    }

    public TaxesDao getTaxesDao() {
        return taxesDao;
    }

    public UnitsDao getUnitsDao() {
        return unitsDao;
    }

    public PricesDao getPricesDao() {
        return pricesDao;
    }

    public ClientBarcodesDao getClientBarcodesDao() {
        return clientBarcodesDao;
    }

    public DivisionsDao getDivisionsDao() {
        return divisionsDao;
    }

    public UserDivisionsDao getUserDivisionsDao() {
        return userDivisionsDao;
    }

    public ItemDivisionsDao getItemDivisionsDao() {
        return itemDivisionsDao;
    }

    public ItemVolumesDao getItemVolumesDao() {
        return itemVolumesDao;
    }

    public ClientAreasDao getClientAreasDao() {
        return clientAreasDao;
    }

    public ClientItemCodesDao getClientItemCodesDao() {
        return clientItemCodesDao;
    }

    public ClientContactsDao getClientContactsDao() {
        return clientContactsDao;
    }

    public ClientPropertyValuesDao getClientPropertyValuesDao() {
        return clientPropertyValuesDao;
    }

    public UserCyclesDao getUserCyclesDao() {
        return userCyclesDao;
    }

    public ItemBarcodesDao getItemBarcodesDao() {
        return itemBarcodesDao;
    }

    public ClientPropertiesDao getClientPropertiesDao() {
        return clientPropertiesDao;
    }

    public ClientPriceListsDao getClientPriceListsDao() {
        return clientPriceListsDao;
    }

    public PriceListsDao getPriceListsDao() {
        return priceListsDao;
    }

    public ObjectiveAssignmentsDao getObjectiveAssignmentsDao() {
        return objectiveAssignmentsDao;
    }

    public CycleCallsDao getCycleCallsDao() {
        return cycleCallsDao;
    }

    public ClientCurrenciesDao getClientCurrenciesDao() {
        return clientCurrenciesDao;
    }

    public ClientPropertyLevelsDao getClientPropertyLevelsDao() {
        return clientPropertyLevelsDao;
    }

    public ClientItemHistoryDao getClientItemHistoryDao() {
        return clientItemHistoryDao;
    }

    public AssetsStatusDao getAssetsStatusDao() {
        return assetsStatusDao;
    }

    public ClientDivisionsDao getClientDivisionsDao() {
        return clientDivisionsDao;
    }

    public ClientAvailabilitiesDao getClientAvailabilitiesDao() {
        return clientAvailabilitiesDao;
    }

    public ClientMustStockListDao getClientMustStockListDao() {
        return clientMustStockListDao;
    }

    public CurrenciesDao getCurrenciesDao() {
        return currenciesDao;
    }

    public CompetitorItemsDao getCompetitorItemsDao() {
        return competitorItemsDao;
    }

    public PaymentTermsDao getPaymentTermsDao() {
        return paymentTermsDao;
    }

    public BlankVisitsDao getBlankVisitsDao() {
        return blankVisitsDao;
    }

    public JourneysDao getJourneysDao() {
        return journeysDao;
    }

    public BlankVisitContactsDao getBlankVisitContactsDao() {
        return blankVisitContactsDao;
    }

    public VisitHistoryDao getVisitHistoryDao() {
        return visitHistoryDao;
    }

    public VisitReasonsDao getVisitReasonsDao() {
        return visitReasonsDao;
    }

    public ReasonsDao getReasonsDao() {
        return reasonsDao;
    }

    public VisitTypesDao getVisitTypesDao() {
        return visitTypesDao;
    }

    public TransactionHeadersDao getTransactionHeadersDao() {
        return transactionHeadersDao;
    }

    public ClientDocumentImagesDao getClientDocumentImagesDao() {
        return clientDocumentImagesDao;
    }

    public ClientStockCountDetailsDao getClientStockCountDetailsDao() {
        return clientStockCountDetailsDao;
    }

    public ClientItemClassificationHistoryDao getClientItemClassificationHistoryDao() {
        return clientItemClassificationHistoryDao;
    }

    public WareHouseBarcodeScanningDao getWareHouseBarcodeScanningDao() {
        return wareHouseBarcodeScanningDao;
    }

    public ClientSellingSuggestionDao getClientSellingSuggestionDao() {
        return clientSellingSuggestionDao;
    }

    public TransactionPromotionDetailsDao getTransactionPromotionDetailsDao() {
        return transactionPromotionDetailsDao;
    }

    public TransactionDetailsMissedMSLDao getTransactionDetailsMissedMSLDao() {
        return transactionDetailsMissedMSLDao;
    }

    public ClientStockCountHeadersDao getClientStockCountHeadersDao() {
        return clientStockCountHeadersDao;
    }

    public DailyTargetAchievementsDao getDailyTargetAchievementsDao() {
        return dailyTargetAchievementsDao;
    }

    public ClientItemClassificationLevelsDao getClientItemClassificationLevelsDao() {
        return clientItemClassificationLevelsDao;
    }

    public ClientItemClassificationsDao getClientItemClassificationsDao() {
        return clientItemClassificationsDao;
    }

    public VisitsDao getVisitsDao() {
        return visitsDao;
    }

    public SuggestedUserLoadDao getSuggestedUserLoadDao() {
        return suggestedUserLoadDao;
    }

    public PromotionHeadersDao getPromotionHeadersDao() {
        return promotionHeadersDao;
    }

    public SurveyAssignmentsDao getSurveyAssignmentsDao() {
        return surveyAssignmentsDao;
    }

    public SurveyQuestionsDao getSurveyQuestionsDao() {
        return surveyQuestionsDao;
    }

    public SubQuestionAnswersDao getSubQuestionAnswersDao() {
        return subQuestionAnswersDao;
    }

    public MovementDetailsDao getMovementDetailsDao() {
        return movementDetailsDao;
    }

    public PromotionAssignmentsDao getPromotionAssignmentsDao() {
        return promotionAssignmentsDao;
    }

    public VehiclesDao getVehiclesDao() {
        return vehiclesDao;
    }

    public DeviceTrackingDao getDeviceTrackingDao() {
        return deviceTrackingDao;
    }

    public VehiclesStockDao getVehiclesStockDao() {
        return vehiclesStockDao;
    }

    public WareHouseBarcodesDao getWareHouseBarcodesDao() {
        return wareHouseBarcodesDao;
    }

    public TransactionSequencesDao getTransactionSequencesDao() {
        return transactionSequencesDao;
    }

    public SurveyAnswersDao getSurveyAnswersDao() {
        return surveyAnswersDao;
    }

    public PromotionDetailsDao getPromotionDetailsDao() {
        return promotionDetailsDao;
    }

    public QuestionAnswersDao getQuestionAnswersDao() {
        return questionAnswersDao;
    }

    public MovementHeadersDao getMovementHeadersDao() {
        return movementHeadersDao;
    }

    public SurveyImagesDao getSurveyImagesDao() {
        return surveyImagesDao;
    }

    public DeviceSerialsDao getDeviceSerialsDao() {
        return deviceSerialsDao;
    }

    public SurveysDao getSurveysDao() {
        return surveysDao;
    }

    public TransactionDetailsDao getTransactionDetailsDao() {
        return transactionDetailsDao;
    }

    public ClientUserCollectionDao getClientUserCollectionDao() {
        return clientUserCollectionDao;
    }

    public CollectionInvoicesDao getCollectionInvoicesDao() {
        return collectionInvoicesDao;
    }

    public TotalClientDuesDao getTotalClientDuesDao() {
        return totalClientDuesDao;
    }

    public WarehouseQuantitiesDao getWarehouseQuantitiesDao() {
        return warehouseQuantitiesDao;
    }

    public MoneyCategoryDao getMoneyCategoryDao() {
        return moneyCategoryDao;
    }

    public CollectionDetailsDao getCollectionDetailsDao() {
        return collectionDetailsDao;
    }

    public CollectionImagesDao getCollectionImagesDao() {
        return collectionImagesDao;
    }

    public ClientDuesDao getClientDuesDao() {
        return clientDuesDao;
    }

    public CollectionHeadersDao getCollectionHeadersDao() {
        return collectionHeadersDao;
    }

    public BrandsDao getBrandsDao() {
        return brandsDao;
    }

    public ClientAssetsPicturesDao getClientAssetsPicturesDao() {
        return clientAssetsPicturesDao;
    }

    public ShareOfShelfTrackerDao getShareOfShelfTrackerDao() {
        return shareOfShelfTrackerDao;
    }

    public SOSTrackerHeadersDao getSOSTrackerHeadersDao() {
        return sOSTrackerHeadersDao;
    }

    public UserVersionsDao getUserVersionsDao() {
        return userVersionsDao;
    }

    public UserSpecialPriceListsDao getUserSpecialPriceListsDao() {
        return userSpecialPriceListsDao;
    }

    public ForceSyncJourneysDao getForceSyncJourneysDao() {
        return forceSyncJourneysDao;
    }

    public NewSkuListDivisionsDao getNewSkuListDivisionsDao() {
        return newSkuListDivisionsDao;
    }

    public SOSTargetAssignmentDao getSOSTargetAssignmentDao() {
        return sOSTargetAssignmentDao;
    }

    public ClientMouvementStockDao getClientMouvementStockDao() {
        return clientMouvementStockDao;
    }

    public SOSTrackerDetailsDao getSOSTrackerDetailsDao() {
        return sOSTrackerDetailsDao;
    }

    public SOSTargetHeadersDao getSOSTargetHeadersDao() {
        return sOSTargetHeadersDao;
    }

    public DuoUsersDao getDuoUsersDao() {
        return duoUsersDao;
    }

    public ClientNewSkuListDao getClientNewSkuListDao() {
        return clientNewSkuListDao;
    }

    public ObjectivesDao getObjectivesDao() {
        return objectivesDao;
    }

    public SOSBrandsDao getSOSBrandsDao() {
        return sOSBrandsDao;
    }

    public SOSCategoryBrandsDao getSOSCategoryBrandsDao() {
        return sOSCategoryBrandsDao;
    }

    public MSLDivisionsDao getMSLDivisionsDao() {
        return mSLDivisionsDao;
    }

    public ShareOfShelvesDao getShareOfShelvesDao() {
        return shareOfShelvesDao;
    }

    public SOSCategoriesDao getSOSCategoriesDao() {
        return sOSCategoriesDao;
    }

    public SOSTargetDetailsDao getSOSTargetDetailsDao() {
        return sOSTargetDetailsDao;
    }

    public ClientAssetStatusDao getClientAssetStatusDao() {
        return clientAssetStatusDao;
    }

    public ClientCreditingsDao getClientCreditingsDao() {
        return clientCreditingsDao;
    }

    public TargetAssignmentsDao getTargetAssignmentsDao() {
        return targetAssignmentsDao;
    }

    public ObjectivePrioritiesDao getObjectivePrioritiesDao() {
        return objectivePrioritiesDao;
    }

    public TargetHeadersDao getTargetHeadersDao() {
        return targetHeadersDao;
    }

    public TargetDetailsDao getTargetDetailsDao() {
        return targetDetailsDao;
    }

    public ObjectiveAchievementsDao getObjectiveAchievementsDao() {
        return objectiveAchievementsDao;
    }

    public TargetAchievementsDao getTargetAchievementsDao() {
        return targetAchievementsDao;
    }

}
