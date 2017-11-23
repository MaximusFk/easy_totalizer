package com.easytotalizer.contracts;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.utils.Convert;

public class EasyTotalizerTests {

	private Web3j web3j;
	private Credentials credentials;
	private EasyTotalizer defaultEasyTotalizer;
	
	private static Process testrpc;
	
	private static final String DEFAULT_PRIVATE_KEY = "0116af719d217c12310c72c27a13509f192588735ae329f7c4cfef0e1518955e";
	private static final String SECONDARY_PRIVATE_KEY = "8d29e3af97a40a7c55eceaf6dcc2f7dcd78085e45fc2d47047f8508d7f9a70f6";
	private static final String DEFAULT_TITLE = "Simple title";
	private static final String DEFAULT_DESCRIPTION = "Simple description";
	private static final Integer DEFAULT_MIN_BET_ETHER = 5;
	private static final Integer DEFAULT_PERCENT = 10;
	private static final String DEFAULT_VARIANT_1 = "alfa";
	private static final Integer DEFAULT_VARIANT_1_INDEX = 0;
	private static final String DEFAULT_VARIANT_2 = "gamma";
	private static final Integer DEFAULT_VARIANT_2_INDEX = 1;
	private static final String DEFAULT_VARIANT_3 = "omega";
	private static final Integer DEFAULT_VARIANT_3_INDEX = 2;
	
	private Integer DEFAULT_VARIANT_1_BETS = 0;
	private Integer DEFAULT_VARIANT_2_BETS = 0;
	private Integer DEFAULT_VARIANT_3_BETS = 0;
	private BigInteger DEFAULT_VARIANT_COUNT = BigInteger.valueOf(3);
	
	@BeforeClass
	public static void setUpProcess() throws IOException {
		testrpc = new ProcessBuilder(
				"testrpc",
				"--account=0x" + DEFAULT_PRIVATE_KEY + ",1000000000000000000000",
				"--account=0x" + SECONDARY_PRIVATE_KEY + ",1000000000000000000000"
			).start();
	}
	
	@AfterClass
	public static void resetProcess() {
		testrpc.destroyForcibly();
	}
	
	@Before
	public void setUp() throws Exception {
		web3j = Web3j.build(new HttpService());
		credentials = Credentials.create(DEFAULT_PRIVATE_KEY);
		
		List<byte[]> variants = new ArrayList<>();
		variants.add(Arrays.copyOf(DEFAULT_VARIANT_1.getBytes(), 32));
		variants.add(Arrays.copyOf(DEFAULT_VARIANT_2.getBytes(), 32));
		variants.add(Arrays.copyOf(DEFAULT_VARIANT_3.getBytes(), 32));
		defaultEasyTotalizer = EasyTotalizer.deploy(
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT
			).send();
		defaultEasyTotalizer.init(
				DEFAULT_TITLE,
				DEFAULT_DESCRIPTION,
				Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger(),
				BigInteger.valueOf(DEFAULT_PERCENT),
				variants
			).send();
		defaultEasyTotalizer.bet(
				BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX),
				Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()
			).send();
		DEFAULT_VARIANT_1_BETS = 1;
	}

	@Test
	public void testClose() throws Exception {
		Credentials credentials = Credentials.create(SECONDARY_PRIVATE_KEY);
		EasyTotalizer easyTotalizer = EasyTotalizer.load(
				defaultEasyTotalizer.getContractAddress(),
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT
			);
		easyTotalizer.bet(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX), Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()).send();
		this.defaultEasyTotalizer.close(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX)).send();
		assertThat(this.defaultEasyTotalizer.isClosed().send()).isEqualTo(true);
	}

	@Test
	public void testTitle() throws Exception {
		assertThat(this.defaultEasyTotalizer.title().send()).isEqualTo(DEFAULT_TITLE);
	}

	@Test
	public void testVariants() throws Exception {
		Tuple2<byte[], BigInteger> variant_1 = this.defaultEasyTotalizer.variants(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX)).send();
		assertThat((new String(variant_1.getValue1()))).isEqualToIgnoringWhitespace(DEFAULT_VARIANT_1);
		assertThat(variant_1.getValue2()).isEqualTo(BigInteger.valueOf(DEFAULT_VARIANT_1_BETS));
		
		Tuple2<byte[], BigInteger> variant_2 = this.defaultEasyTotalizer.variants(BigInteger.valueOf(DEFAULT_VARIANT_2_INDEX)).send();
		assertThat(new String(variant_2.getValue1())).isEqualToIgnoringWhitespace(DEFAULT_VARIANT_2);
		assertThat(variant_2.getValue2()).isEqualTo(BigInteger.valueOf(DEFAULT_VARIANT_2_BETS));
		
		Tuple2<byte[], BigInteger> variant_3 = this.defaultEasyTotalizer.variants(BigInteger.valueOf(DEFAULT_VARIANT_3_INDEX)).send();
		assertThat(new String(variant_3.getValue1())).isEqualToIgnoringWhitespace(DEFAULT_VARIANT_3);
		assertThat(variant_3.getValue2()).isEqualTo(BigInteger.valueOf(DEFAULT_VARIANT_3_BETS));
	}

	@Test
	public void testOrganizer() throws Exception {
		assertThat(this.defaultEasyTotalizer.organizer().send()).isEqualTo(credentials.getAddress());
	}

	@Test
	public void testPercent() throws Exception {
		assertThat(this.defaultEasyTotalizer.percent().send().intValue()).isEqualTo(DEFAULT_PERCENT);
	}

	@Test
	public void testTakeReward() throws Exception {
		Credentials credentials = Credentials.create(SECONDARY_PRIVATE_KEY);
		EasyTotalizer easyTotalizer = EasyTotalizer.load(
				defaultEasyTotalizer.getContractAddress(),
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT
			);
		easyTotalizer.bet(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX), Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()).send();
		BigInteger beforeBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
		this.defaultEasyTotalizer.close(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX)).send();
		assertThat(this.defaultEasyTotalizer.isClosed().send()).isEqualTo(true);
		easyTotalizer.takeReward().send();
		BigInteger afterBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
		assertThat(afterBalance).isGreaterThan(beforeBalance);
	}

	@Test
	public void testDescription() throws Exception {
		assertThat(this.defaultEasyTotalizer.description().send()).isEqualTo(DEFAULT_DESCRIPTION);
	}

	@Test
	public void testBet() throws Exception {
		Credentials credentials = Credentials.create(SECONDARY_PRIVATE_KEY);
		EasyTotalizer easyTotalizer = EasyTotalizer.load(
				defaultEasyTotalizer.getContractAddress(),
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT
			);
		easyTotalizer.bet(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX), Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()).send();
		
	}

	@Test
	public void testBank() throws Exception {
		BigInteger currentBank = this.defaultEasyTotalizer.bank().send();
		assertThat(currentBank).isEqualTo(Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger());
	}

	@Test
	public void testIsClosed() throws Exception {
		Credentials credentials = Credentials.create(SECONDARY_PRIVATE_KEY);
		EasyTotalizer easyTotalizer = EasyTotalizer.load(
				defaultEasyTotalizer.getContractAddress(),
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT
			);
		easyTotalizer.bet(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX), Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()).send();
		this.defaultEasyTotalizer.close(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX)).send();
		assertThat(this.defaultEasyTotalizer.isClosed().send()).isEqualTo(true);
	}

	@Test
	public void testMinimumBet() throws Exception {
		assertThat(this.defaultEasyTotalizer.minimumBet().send())
		.isEqualTo(Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger());
	}

	@Test
	public void testPayAmount() throws Exception {
		this.defaultEasyTotalizer.close(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX)).send();
		BigInteger bank = this.defaultEasyTotalizer.bank().send();
		BigInteger orginizerPaiment = bank.divide(BigInteger.valueOf(100L)).multiply(BigInteger.valueOf(DEFAULT_PERCENT));
		BigInteger payAmount = bank.subtract(orginizerPaiment).divide(BigInteger.valueOf(DEFAULT_VARIANT_1_BETS));
		assertThat(this.defaultEasyTotalizer.payAmount().send()).isEqualTo(payAmount);
	}

	@Test
	public void testWinner() throws Exception {
		this.defaultEasyTotalizer.close(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX)).send();
		Tuple2<byte[], BigInteger> winner = this.defaultEasyTotalizer.winner().send();
		assertThat(new String(winner.getValue1())).isEqualToIgnoringWhitespace(DEFAULT_VARIANT_1);
		assertThat(winner.getValue2()).isEqualTo(BigInteger.valueOf(DEFAULT_VARIANT_1_BETS));
	}
	
	@Test
	public void testGetVariantsCount() throws Exception {
		assertThat(this.defaultEasyTotalizer.getVariantsCount().send()).isEqualTo(DEFAULT_VARIANT_COUNT);
	}

	@Test
	public void testDeploy() throws Exception {
		List<byte[]> variants = new ArrayList<>();
		variants.add(Arrays.copyOf("alfa".getBytes(), 32));
		variants.add(Arrays.copyOf("gamma".getBytes(), 32));
		variants.add(Arrays.copyOf("omega".getBytes(), 32));
		EasyTotalizer easyTotalizer = EasyTotalizer.deploy(
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT
			).send();
		easyTotalizer.init(
				"Test title",
				"Test description",
				Convert.toWei(new BigDecimal(5), Convert.Unit.ETHER).toBigInteger(),
				BigInteger.valueOf(10),
				variants
			).send();
		assertThat(easyTotalizer.isValid()).isTrue();
	}

	@Test
	public void testLoad() throws IOException {
		EasyTotalizer easyTotalizer = EasyTotalizer.load(
				defaultEasyTotalizer.getContractAddress(),
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT
			);
		assertThat(easyTotalizer.isValid()).isTrue();
	}
	
	@Test
	public void testKill() throws Exception {
		defaultEasyTotalizer.kill().send();
		assertThat(defaultEasyTotalizer.isValid()).isFalse();
	}
	
	@Test
	public void testGetClosedEvents() throws Exception {
		TransactionReceipt transactionReceipt = defaultEasyTotalizer.close(BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX)).send();
		assertThat(defaultEasyTotalizer.getClosedEvents(transactionReceipt)).isNotEmpty();
	}
	
	@Test
	public void testGetBetMadeEvents() throws Exception {
		Credentials credentials = Credentials.create(SECONDARY_PRIVATE_KEY);
		EasyTotalizer easyTotalizer = EasyTotalizer.load(
				defaultEasyTotalizer.getContractAddress(),
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT
			);
		TransactionReceipt transactionReceipt = easyTotalizer.bet(
				BigInteger.valueOf(DEFAULT_VARIANT_1_INDEX),
				Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()
			).send();
		assertThat(easyTotalizer.getBetMadeEvents(transactionReceipt)).isNotEmpty();
	}
}
