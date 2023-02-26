from UtilityFunctions import *

if __name__ == "main":
    # open customer file and read lines to list of customers
    customerFile = open("customerNames.csv")
    customers = customerFile.readlines()

    # get menu file
    menuFileName = input("Enter menu file name: ")
    menu = openMenu(menuFileName)

    # convert menu file into a dict of menu weights
    weightedMenu = createMenuWeights(menu)

    # get user input for start date and end date
    # convert into date object
    dateFormat = "%Y-%M-%D"

    startDateString = input("Enter start date (Y-M-D): ")
    numDaysToGenerate = input("Enter a number of days to generate ")

    startDate = datetime.strptime(startDateString, dateFormat).date()

    # get user input for game days
    gameDaysInput = input(
        "enter a comma separated list of game days (Y-M-D): ")
    # parse the game days into a set
    gameDays = parseGameDaysInput(gameDaysInput)

    # open appropriate output files
    orderItemFile = open("OrderItem.csv", "w")
    soldItemFile = open("SoldItem.csv", "w")

    # main loop for each day in the range
    curDate = startDate
    for i in range(numDaysToGenerate):

        # - check if game day and generate a number of orders for that day
        numberOfOrders = numOrdersForDay(curDate, gameDays)
        # - for each order
        for nthOrder in range(numberOfOrders):
            # -- generate a number of items for that order
            numItems = numItemsForOrder()
            # -- choose items for the order
            items = selectSoldItems(weightedMenu, numItems)
            # -- get prices for all those items and create an order with employee id and a customer name
            order = createOrder(items)
            # -- create a string for the order
            orderString = createStringOfOrder(order)
            # -- create a string for each item sold
            itemsString = createStringOfSoldItems(items)
            # -- write the strings to the output files
            orderItemFile.write(orderString)
            soldItemFile.write(itemsString)
            # - increment date
            curDate = incrementDate(curDate)

    # close files
    orderItemFile.close()
    soldItemFile.close()
