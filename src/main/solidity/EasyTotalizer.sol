pragma solidity 0.4.18;
import "./node_modules/zeppelin-solidity/contracts/math/SafeMath.sol";


contract EasyTotalizer {
    
    struct Variant {
        bytes32 name;
        uint8 betsCount;
    }
    
    struct Bet {
        bool betted;
        bool paid;
        uint variant;
    }

    using SafeMath for uint;

    uint public constant MAX_VARIANTS = 32;
    
    string public title;
    string public description;
    uint public minimumBet;
    uint public bank;
    address public organizer;
    uint8 public percent;
    Variant[] public variants;
    Variant public winner;
    mapping(address => Bet) bets;
    
    bool public isClosed;
    uint public payAmount;
    
    event Closed(uint _winner);
    event BetMade(uint _variant);
    
    modifier variantExist(uint _variant) {
        require(_variant < variants.length);
        _;
    }
    
    modifier onlyOrganizer() {
        require(msg.sender == organizer);
        _;
    }
    
    function EasyTotalizer() public {
        organizer = msg.sender;
    }

    /**
     * Initialize totalizer
     * 
     * @param _title Title of this event
     * @param _desc Description of this event
     * @param _minBet Minimum bet in wei
     * @param _percent Percentage of payment to the organizer
     * @param _variants Names list of variants
     */
    function init(
        string _title,
        string _desc,
        uint _minBet,
        uint8 _percent,
        bytes32[] _variants
    )
        external
        onlyOrganizer
    {
        title = _title;
        description = _desc;
        minimumBet = _minBet;
        percent = _percent;
        for (uint i = 0; i < _variants.length && i < MAX_VARIANTS; i++) {
            variants.push(Variant({name: _variants[i], betsCount: 0}));
        }
    }
    
    /**
     * Make a bet
     * 
     * @param _variant Variant identifier
     */ 
    function bet(uint _variant)
        external
        payable
        variantExist(_variant)
    {
        require(!isClosed);
        require(msg.value >= minimumBet);
        require(!bets[msg.sender].betted);
        
        bets[msg.sender] = Bet({betted: true, variant: _variant, paid: false});
        bank = bank.add(msg.value);
        variants[_variant].betsCount++;
        BetMade(_variant);
    }
    
    /**
     * Send reward to sender if is maked bet on winner and event is closed
     * 
     * @return Reward amount
     */
    function takeReward() external returns(uint) {
        require(isClosed);
        require(!bets[msg.sender].paid);
        
        if (payAmount > 0) {
            bets[msg.sender].paid = true;
            if (!msg.sender.send(payAmount)) {
                bets[msg.sender].paid = false;
                return 0;
            }
        }
        return payAmount;
    }
    
    /**
     * Close this event and send percentage payment to organizer
     * 
     * @param _winner Sets winner identifier
     */
    function close(uint _winner) external onlyOrganizer variantExist(_winner) {
        require(!isClosed);
        isClosed = true;
        winner = variants[_winner];
        uint organizerPaiment = calculatePayment();
        payAmount = bank.sub(organizerPaiment).div(winner.betsCount);
        organizer.transfer(organizerPaiment);
        Closed(_winner);
    }
    
    function getVariantsCount() external constant returns(uint) {
    	return variants.length;
    }
    
    function calculatePayment() internal constant returns(uint) {
    	return bank.mul(percent).div(100);
    }
    
    function kill() public onlyOrganizer {
        selfdestruct(msg.sender);
    }
    
    function () public payable onlyOrganizer {}
}
