package com.github.sawied.microservice.gateway.ews;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.DefaultExtendedPropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.SortDirection;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.MessageDisposition;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.response.ResponseMessage;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.AttachmentCollection;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.InternetMessageHeader;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.property.definition.PropertyDefinitionBase;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

public class ExchangeAPI implements InitializingBean {
	
	private static final UUID CID = UUID.fromString("01638372-9F96-43b2-A403-B504ED14A910");
	
	private static final Logger LOG = LoggerFactory.getLogger(ExchangeAPI.class);
	
	private String MAIL_RETRIEVED = "CASE_MAIL_RETRIEVED";
	
	private static int DEFAULT_PAGE_SIZE = 10;

	@Autowired
	private CustomExchangeService exchangeService;


	
	@Value("ews.email.mailboxs")
	private String emailBoxs=null;
	
	
	private List<String> mailBoxs = null;
	
	
	public void syncEWSMessage(){
		
		if(mailBoxs!=null){
			LOG.info("There are/is {} ews service be configed.",mailBoxs.size());
			
			for(String mail : mailBoxs){
				LOG.info("do sync mail function {}", mail);
				fetchMail(mail);
			}
		}else{
			LOG.error("it seems you don't config exchange server bean in applicationContext.");
		}
	}
	
	
	public void sendMessage() throws ExchangeExCeption {
		EmailMessage msg;
		exchangeService.releaseConnection();
		try {
			msg = new EmailMessage(exchangeService);
			//msg.setFrom(new EmailAddress("noreply@chinasoft.com"));
			msg.setSubject("Hello EWS_T world!");
			msg.setBody(MessageBody.getMessageBodyFromText("Sent using the EWS Java API."));
			msg.getToRecipients().add("zhangxiaowei@chinasofti.com");
			msg.send();
		} catch (Exception e) {
			LOG.error("occur error when send message",e);
			throw new ExchangeExCeption();
		}

	}
	
	/**
	 * fetch mail list that don't be marked,then save mail info into database
	 * @param mailAddress
	 * @param password
	 */
	public void fetchMail(String mail){
		LOG.debug("use email box {} to fetch mail list",mail);
		exchangeService.releaseConnection();
		//
		int offsize = 0;
		
		ItemView view = new ItemView(DEFAULT_PAGE_SIZE + 1 ,offsize);
		
		try{
			view.getOrderBy().add(ItemSchema.DateTimeReceived, SortDirection.Descending);
			ExtendedPropertyDefinition extendedPropertyDefinition = new ExtendedPropertyDefinition(CID, MAIL_RETRIEVED,MapiPropertyType.Integer);
			ExtendedPropertyDefinition PR_TRANSPORT_MESSAGE_HEADERS = new ExtendedPropertyDefinition(0x007D, MapiPropertyType.String);
	       
			PropertySet propertySet = new PropertySet(BasePropertySet.FirstClassProperties,extendedPropertyDefinition,PR_TRANSPORT_MESSAGE_HEADERS);
			view.setPropertySet(propertySet);
			
			//search filter 
			SearchFilter searchFilter = new SearchFilter.Not(new SearchFilter.Exists(extendedPropertyDefinition));
			
			boolean moreItems = true;
			ItemId anchorId = null;
			
			//final Mailbox mailbox = new Mailbox(mail);
			FolderId folderId = new FolderId(WellKnownFolderName.Inbox);
			
			while(moreItems){
				FindItemsResults<Item> findItems = exchangeService.findItems(folderId,/** searchFilter,**/ view);
				moreItems=findItems.isMoreAvailable();
				if(moreItems && anchorId !=null){
					if(findItems.getItems().get(0).getId()!=anchorId){
						LOG.warn("The collection has changed while paging. Some results may be missed.");
					}
				}
				
				LOG.info("fetch mail from exchang server view offsize {}",view.getOffset());
				if(moreItems){
					view.setOffset(view.getOffset() + DEFAULT_PAGE_SIZE);
				}
				
				int i = findItems.getItems().size();
				if(i>0){
					anchorId =findItems.getItems().get(i-1).getId();
				}else {
					LOG.info("no mail be fetched.");
					break;
				}
				
				int displayCount = i > DEFAULT_PAGE_SIZE ? DEFAULT_PAGE_SIZE : i;
				
			     exchangeService.loadPropertiesForItems(findItems, propertySet);
			     
			     for(int j = 0; j < displayCount; j++){
			    	 Item item = findItems.getItems().get(j);
			    	 if(item instanceof EmailMessage){
			    		 EmailMessage emailMessage = (EmailMessage) item;
			    		 printEmailMessage(emailMessage);
			    		 emailMessage.setExtendedProperty(extendedPropertyDefinition, 1);
			    		 exchangeService.updateItem(item, FolderId.getFolderIdFromWellKnownFolderName(WellKnownFolderName.Inbox), ConflictResolutionMode.AutoResolve, MessageDisposition.SaveOnly, null);
			    	 }
			     }
			}
			
		}catch(Exception e){
			LOG.error("occur error when fetch email.",e);
		}
		
	}
	
	private void printEmailMessage(EmailMessage email) throws Exception {
		
		String subject = email.getSubject();
		ItemId itemId = email.getId();
		String from = email.getFrom().getAddress();
		Date dateTimeCreated = email.getDateTimeCreated();
		Date dateTimeReceived = email.getDateTimeReceived();
		Date dateTimeSent = email.getDateTimeSent();
		String displayTo = email.getDisplayTo();
		MessageBody body = email.getBody();
		BodyType type=body.getBodyType();
		
		String messageBodyString= MessageBody.getStringFromMessageBody(body);
		
		String textOnly = Jsoup.parse(messageBodyString).text();
		//print info 
		
		String internetMessageId = email.getInternetMessageId();
		
		System.out.println("internetMessageId :" + internetMessageId);
		
		String references = email.getReferences();
		System.out.println("references :" + getOrignReference(references));
		
		System.out.println("Subject: " + subject);
		System.out.println("created: " + dateTimeCreated);
		System.out.println("received: " + dateTimeReceived);
		System.out.println("sent: " + dateTimeSent);
		System.out.println("from: " + from);
		System.out.println("type: " + type);
		System.out.println("displayTo: " + displayTo);
		System.out.println("body: " + body.toString());
		System.out.println("body:" + messageBodyString);
		System.out.println("text only:" + textOnly);
		System.out.println("itemId:" + itemId.getUniqueId());
		
	}
	
	/**
	 * get reference of first one that represent the orign mail messageId
	 * @param references
	 * @return
	 */
	public String getOrignReference(String references){
		String orign = null;
		if(references!=null) {
			String removedBlankString  = references.trim();
			
			if(removedBlankString.startsWith("<")) {
				int indexOf = removedBlankString.indexOf(">");
				orign=removedBlankString.substring(0, indexOf+1);
			}
		}
			return orign;
	}
	
	public void releaseConnection() {
		this.exchangeService.releaseConnection();
	}

	
	@Override
	public void afterPropertiesSet() throws Exception {
		// init mailbox
		if(emailBoxs!=null){
			final String[] mailsAddress = emailBoxs.split(",");
			this.mailBoxs = Arrays.asList(mailsAddress);
			 
		}else{
			LOG.error("mail box setting is empty.");
		}
		
		
	}


	public void replyMail(String id) {
		exchangeService.releaseConnection();
		try {
			ItemId itemId = ItemId.getItemIdFromString(id);
			EmailMessage emailMessage = EmailMessage.bind(exchangeService, itemId);
	
			// ExtendedPropertyDefinition xExperimentalHeader = new ExtendedPropertyDefinition(DefaultExtendedPropertySet,"X-Experimental", MapiPropertyType.String);
			
			
			ResponseMessage createReply = emailMessage.createReply(true);
		
			
			MessageBody msgBody = new MessageBody("<p>reply message test</p>");
			msgBody.setBodyType(BodyType.HTML);
			createReply.setBodyPrefix(msgBody);
			
			createReply.sendAndSaveCopy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			
	}

}
