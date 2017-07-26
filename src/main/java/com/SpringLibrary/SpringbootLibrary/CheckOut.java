package com.SpringLibrary.SpringbootLibrary;

/**
 * Created by ricky.clevinger on 7/12/2017.
 */
import Model.Book;
import Model.Member;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

import static Resource.gridHelper.lNameFilterGridChange;
import static Resource.gridHelper.titleFilterGridChange;
import static com.SpringLibrary.SpringbootLibrary.LibraryUI.getLibraryViewDisplay;

@SpringView(name = CheckOut.VIEW_NAME)
public class CheckOut extends VerticalLayout implements View {
    public static final String VIEW_NAME = "CheckOut";

    HorizontalLayout hLayout;
    Button checkOut;
    private TextField titleFilter;   // TextField will be used to filter the results on the grid.
    private TextField authorFilter;   // TextField will be used to filter the results on the grid.
    String titleId;  // Id used to determine which item is selected in the grid.
    String memberId;  // Id used to determine which item is selected in the grid.
    RestTemplate restTemplate = new RestTemplate();  // RestTemplate used to make calls to micro-service.
    List<Book> books; // Used to store data retrieved from micro-service. Placed into the grid.
    List<Member> members; // Used to store data retrieved from micro-service. Placed into the grid.
    Grid<Member> memberGrid;
    Grid<Book> bookGrid;

    @Value("${my.bookUrl}")
    private String bookUrl;


    @PostConstruct
    void init() {

        getLibraryViewDisplay().setSizeFull();
        createLayout();
        createFilter();
        createBookGrid();
        createMemberGrid();
        addCheckOutButton();
    }

    private void addCheckOutButton() {


        checkOut = new Button ("Check Out");

        checkOut.addClickListener(event ->
        {
            try{

            this.restTemplate.getForObject(bookUrl + "/trans/insert/" + this.titleId + "/" + 2 + "/" + memberId, String.class);
            this.restTemplate.getForObject(bookUrl + "/books/cho/" + this.titleId + "/" + 2 + "/" + memberId, String.class);
            getUI().getNavigator().navigateTo(CheckOut.VIEW_NAME);

            }
            catch (ResourceAccessException error)
            {

                Notification.show("Service unavailable, please try again in a few minutes");

            }
        });

        addComponent(checkOut);

    }
    private void createMemberGrid() {

        try{
        members = Arrays.asList(restTemplate.getForObject(bookUrl + "/members/all", Member[].class));
        memberGrid = new Grid<>();
        memberGrid.setWidth(100, Unit.PERCENTAGE);

        // Retrieves the data from the book micro-service.


        // Create a grid and adds listener to record selected item.
        memberGrid = new Grid<>();
        memberGrid.addSelectionListener(event -> {
            this.memberId = event.getFirstSelectedItem().get().getId() + "";
        });

        // Sets the width of the grid.
        memberGrid.setWidth(100, Unit.PERCENTAGE);
        // Sets list to the grid
        memberGrid.setItems(members);
        //Specifies what parts of the objects in the grid are shown.
        memberGrid.addColumn(Member::getFName, new TextRenderer()).setCaption("First Name");
        memberGrid.addColumn(Member::getLName, new TextRenderer()).setCaption("Last Name");

        hLayout.setSizeFull();
        memberGrid.setSizeFull();
        hLayout.addComponent(memberGrid);

        }
        catch (ResourceAccessException error)
        {

            Notification.show("The Book Service is currently unavailable. Please try again in a "+"" +
                    "few minutes");
        }
    }

    private void createBookGrid() {


        try {
            books = Arrays.asList(restTemplate.getForObject(bookUrl + "/books/check/1", Book[].class));
            bookGrid = new Grid<>();

            bookGrid.addSelectionListener(event -> {
                this.titleId = event.getFirstSelectedItem().get().getBookId() + "";
            });

            // Sets the width of the grid.
            bookGrid.setWidth(100, Unit.PERCENTAGE);
            // Sets list to the grid
            bookGrid.setItems(books);
            //Specifies what parts of the objects in the grid are shown.
            bookGrid.addColumn(Book::getTitle, new TextRenderer()).setCaption("Title");
            bookGrid.addColumn(Book ->
                    Book.getAuthFName() + " " + Book.getAuthLName()).setCaption("Author");

            bookGrid.setWidth(100, Unit.PERCENTAGE);

            hLayout.setSizeFull();
            bookGrid.setSizeFull();
            hLayout.addComponent(bookGrid);

        }
        catch (ResourceAccessException error)
        {

            Notification.show("The Book Service is currently unavailable. Please try again in a "+"" +
                    "few minutes");
        }

    }

    private void createLayout() {

        hLayout = new HorizontalLayout();
        hLayout.setSpacing(true);
        addComponent(hLayout);

    }

    /**
     * Function shall add the search filter to the page.
     * User shall type in part of a book title and the grid will changed accordingly.
     *
     * last modified by ricky.clevinger 7/19/17
     */
    public void createFilter()
    {
        titleFilter = new TextField();
        titleFilter.setWidth(100, Unit.PERCENTAGE);
        titleFilter.setPlaceholder("Title...");
        titleFilter.addValueChangeListener(event -> {

                try {
                    titleFilterGridChange(event, bookGrid);
                }
                catch (NullPointerException error)
                {
                    titleFilter.setValue("");
                    Notification.show("Service unavailable, please try again in a few minutes");

                }

                });
        addComponent(titleFilter);


        authorFilter = new TextField();
        authorFilter.setWidth(100, Unit.PERCENTAGE);
        authorFilter.setPlaceholder("Last Name...");
        authorFilter.addValueChangeListener(event -> {

            try {
                lNameFilterGridChange(event, memberGrid);

            }
            catch (NullPointerException error)
            {
                authorFilter.setValue("");
                Notification.show("Service unavailable, please try again in a few minutes");

            }
        });


        addComponent(authorFilter);

    }//end createFilter


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }
}
