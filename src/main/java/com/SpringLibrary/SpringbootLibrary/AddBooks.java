package com.SpringLibrary.SpringbootLibrary;

import Model.Book;
import Resource.LibraryErrorHelper;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

import static Resource.gridHelper.stringClean;
import static com.SpringLibrary.SpringbootLibrary.LibraryUI.getLibraryViewDisplay;


/**
 * Created by ricky.clevinger on 7/13/2017.
 *
 * last modified by ricky.clevinger on 7/26/17
 */

@SpringView(name = AddBooks.VIEW_NAME)
public class AddBooks extends VerticalLayout implements View
{
    public static final String VIEW_NAME = "addBooks";

    /**
     * Variable Declaration
     */

    private         Label errorDisplay;
    private Grid<Book> bookReturnGrid;
    private String titleId;  // Id used to determine which item is selected in the grid.
    private String memberId;  // Id used to determine which item is selected in the grid.
    private TextField titleFilter;   // TextField will be used to filter the results on the grid.
    private RestTemplate restTemplate = new RestTemplate();  // RestTemplate used to make calls to micro-service.
    private List<Book> books; // Used to store data retrieved from micro-service. Placed into the grid.
    private LibraryErrorHelper errorHelper = new LibraryErrorHelper();//error printer

    /**
     * Variable containing url to access backing service
     */
    @Value("${my.bookMemUrl}")
    private String bookMemUrl;

    /**
     * Initializes the view..
     * Re-sizes the panel
     * Calls addFilters to create search filter for the grid.
     * Calls setupGrid to create and populate the grid.
     * Calls addCheckInButton to create Check In button and add functionality to the button.
     *
     * last modified by ricky.clevinger 7/26/17
     */
    @PostConstruct
    void init()
    {
        getLibraryViewDisplay().setSizeUndefined();
        addBooks();
    }//end init


    /**
     * Creates addbook in button
     * Adds addbook button functionality
     * Add new book to the database
     *
     * last modified by ricky.clevinger 7/31/17
     */
    private void addBooks()
    {
        com.vaadin.ui.TextField title   = new com.vaadin.ui.TextField("Title");
        com.vaadin.ui.TextField fName   = new com.vaadin.ui.TextField("Author: First Name");
        com.vaadin.ui.TextField lName   = new com.vaadin.ui.TextField("Author: Last Name");
        Button addBook                   = new Button("Submit");

        addBook.addClickListener(event ->
        {
            try
            {
                String authLastName = lName.getValue();
                String authFirstName = fName.getValue();
                String bookTitle = title.getValue();

                authFirstName = stringClean(authFirstName);
                authLastName = stringClean(authLastName);
                bookTitle = stringClean(bookTitle);

                if (authLastName.equals("") || authFirstName.equals("") || bookTitle.equals(""))
                {
                    Notification.show("Please Enter the Author's First Name, Last Name, and Book");
                    fName.setValue("");
                    lName.setValue("");
                    title.setValue("");
                }
                else
                {
                    this.restTemplate.getForObject(bookMemUrl + "/books/insert/" + bookTitle + "/"
                            + authFirstName + "/" + authLastName + "/1", String.class);
                    Notification.show(bookTitle + " By " + authFirstName + " "
                            + authLastName + " has been added to the library");
                    title.setValue("");
                    fName.setValue("");
                    lName.setValue("");
                }
            }
            catch (HttpClientErrorException error)
            {
                errorHelper.httpError(error);
                Notification.show("Cannot access Add Book service, please try again in a few minutes");
            }
            catch(NullPointerException error)
            {
                errorHelper.genericError(error);
                Notification.show("Cannot access Add Book service, please try again in a few minutes");
            }
            catch (ResourceAccessException e)
            {
                Notification.show("Cannot access Add Book service, please try again in a few minutes");
                errorHelper.genericError(e);
            }
        });//end add click event
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        addComponents(title,fName,lName, addBook);

    }//end addCheckInButton


    /**
     * Sets a listener that automatically changes the default view when a selection is made
     * @param event on view change
     */
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event)
    {
        // This view is constructed in the init() method()
    }//end enter
}
