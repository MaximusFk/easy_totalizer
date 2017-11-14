package com.easytotalizer.contracts;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
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
		
		defaultEasyTotalizer = EasyTotalizer.deploy(
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT,
				new Utf8String(DEFAULT_TITLE),
				new Utf8String(DEFAULT_DESCRIPTION),
				new Uint256(Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()),
				new Uint8(DEFAULT_PERCENT),
				new Address(credentials.getAddress()),
				new DynamicArray<>(
						new Bytes32(Arrays.copyOf(DEFAULT_VARIANT_1.getBytes(), 32)),
						new Bytes32(Arrays.copyOf(DEFAULT_VARIANT_2.getBytes(), 32)),
						new Bytes32(Arrays.copyOf(DEFAULT_VARIANT_3.getBytes(), 32))
					)
			).send();
		defaultEasyTotalizer.bet(
				new Uint256(DEFAULT_VARIANT_1_INDEX),
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
		easyTotalizer.bet(new Uint256(DEFAULT_VARIANT_1_INDEX), Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()).send();
		this.defaultEasyTotalizer.close(new Uint256(DEFAULT_VARIANT_1_INDEX)).send();
		assertThat(this.defaultEasyTotalizer.isClosed().send().getValue()).isEqualTo(true);
	}

	@Test
	public void testTitle() throws Exception {
		assertThat(this.defaultEasyTotalizer.title().send().getValue()).isEqualTo(DEFAULT_TITLE);
	}

	@Test
	public void testVariants() throws Exception {
		Tuple2<Bytes32, Uint8> variant_1 = this.defaultEasyTotalizer.variants(new Uint256(DEFAULT_VARIANT_1_INDEX)).send();
		assertThat((new String(variant_1.getValue1().getValue()))).isEqualToIgnoringWhitespace(DEFAULT_VARIANT_1);
		assertThat(variant_1.getValue2().getValue()).isEqualTo(BigInteger.valueOf(DEFAULT_VARIANT_1_BETS));
		
		Tuple2<Bytes32, Uint8> variant_2 = this.defaultEasyTotalizer.variants(new Uint256(DEFAULT_VARIANT_2_INDEX)).send();
		assertThat(new String(variant_2.getValue1().getValue())).isEqualToIgnoringWhitespace(DEFAULT_VARIANT_2);
		assertThat(variant_2.getValue2().getValue()).isEqualTo(BigInteger.valueOf(DEFAULT_VARIANT_2_BETS));
		
		Tuple2<Bytes32, Uint8> variant_3 = this.defaultEasyTotalizer.variants(new Uint256(DEFAULT_VARIANT_3_INDEX)).send();
		assertThat(new String(variant_3.getValue1().getValue())).isEqualToIgnoringWhitespace(DEFAULT_VARIANT_3);
		assertThat(variant_3.getValue2().getValue()).isEqualTo(BigInteger.valueOf(DEFAULT_VARIANT_3_BETS));
	}

	@Test
	public void testOrganizer() throws Exception {
		assertThat(this.defaultEasyTotalizer.organizer().send().getValue()).isEqualTo(credentials.getAddress());
	}

	@Test
	public void testPercent() throws Exception {
		assertThat(this.defaultEasyTotalizer.percent().send().getValue().intValue()).isEqualTo(DEFAULT_PERCENT);
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
		easyTotalizer.bet(new Uint256(DEFAULT_VARIANT_1_INDEX), Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()).send();
		BigInteger beforeBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
		this.defaultEasyTotalizer.close(new Uint256(DEFAULT_VARIANT_1_INDEX)).send();
		assertThat(this.defaultEasyTotalizer.isClosed().send().getValue()).isEqualTo(true);
		easyTotalizer.takeReward().send();
		BigInteger afterBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
		assertThat(afterBalance).isGreaterThan(beforeBalance);
	}

	@Test
	public void testDescription() throws Exception {
		assertThat(this.defaultEasyTotalizer.description().send().getValue()).isEqualTo(DEFAULT_DESCRIPTION);
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
		easyTotalizer.bet(new Uint256(DEFAULT_VARIANT_1_INDEX), Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()).send();
		
	}

	@Test
	public void testBank() throws Exception {
		BigInteger currentBank = this.defaultEasyTotalizer.bank().send().getValue();
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
		easyTotalizer.bet(new Uint256(DEFAULT_VARIANT_1_INDEX), Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger()).send();
		this.defaultEasyTotalizer.close(new Uint256(DEFAULT_VARIANT_1_INDEX)).send();
		assertThat(this.defaultEasyTotalizer.isClosed().send().getValue()).isEqualTo(true);
	}

	@Test
	public void testMinimumBet() throws Exception {
		assertThat(this.defaultEasyTotalizer.minimumBet().send().getValue())
		.isEqualTo(Convert.toWei(new BigDecimal(DEFAULT_MIN_BET_ETHER), Convert.Unit.ETHER).toBigInteger());
	}

	@Test
	public void testPayAmount() throws Exception {
		this.defaultEasyTotalizer.close(new Uint256(DEFAULT_VARIANT_1_INDEX)).send();
		BigInteger bank = this.defaultEasyTotalizer.bank().send().getValue();
		BigInteger orginizerPaiment = bank.divide(BigInteger.valueOf(100L)).multiply(BigInteger.valueOf(DEFAULT_PERCENT));
		BigInteger payAmount = bank.subtract(orginizerPaiment).divide(BigInteger.valueOf(DEFAULT_VARIANT_1_BETS));
		assertThat(this.defaultEasyTotalizer.payAmount().send().getValue()).isEqualTo(payAmount);
	}

	@Test
	public void testWinner() throws Exception {
		this.defaultEasyTotalizer.close(new Uint256(DEFAULT_VARIANT_1_INDEX)).send();
		Tuple2<Bytes32, Uint8> winner = this.defaultEasyTotalizer.winner().send();
		assertThat(new String(winner.getValue1().getValue())).isEqualToIgnoringWhitespace(DEFAULT_VARIANT_1);
		assertThat(winner.getValue2().getValue()).isEqualTo(BigInteger.valueOf(DEFAULT_VARIANT_1_BETS));
	}
	
	@Test
	public void testGetVariantsCount() throws Exception {
		assertThat(this.defaultEasyTotalizer.getVariantsCount().send().getValue()).isEqualTo(DEFAULT_VARIANT_COUNT);
	}

	@Test
	public void testDeploy() throws Exception {
		EasyTotalizer easyTotalizer = EasyTotalizer.deploy(
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT,
				new Utf8String("Test title"),
				new Utf8String("Test description"),
				new Uint256(Convert.toWei(new BigDecimal(5), Convert.Unit.ETHER).toBigInteger()),
				new Uint8(10),
				new Address(credentials.getAddress()),
				new DynamicArray<>(
						new Bytes32(Arrays.copyOf("alfa".getBytes(), 32)),
						new Bytes32(Arrays.copyOf("gamma".getBytes(), 32)),
						new Bytes32(Arrays.copyOf("omega".getBytes(), 32))
					)
			).send();
		assertThat(easyTotalizer.isValid()).isEqualTo(true);
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
		assertThat(easyTotalizer.isValid()).isEqualTo(true);
	}

}
