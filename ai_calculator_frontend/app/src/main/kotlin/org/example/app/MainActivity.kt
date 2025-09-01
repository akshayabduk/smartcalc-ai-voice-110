package org.example.app

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// Color palette from spec
private val PRIMARY_COLOR = 0xFF1A73E8.toInt()
private val SECONDARY_COLOR = 0xFF34A853.toInt()
private val ACCENT_COLOR = 0xFFF9AB00.toInt()

/**
 * PUBLIC_INTERFACE
 * MainActivity for Advanced AI Calculator app.
 * Features: Basic/Advanced calculations, voice & natural language input,
 * AI explanation, history, dark/light theme toggle, responsive modern UI.
 */
class MainActivity : Activity() {
    private lateinit var calculationHistory: MutableList<HistoryItem>
    private var isDarkTheme: Boolean = false

    // View refs
    private lateinit var root: View
    private lateinit var inputPanel: LinearLayout
    private lateinit var inputField: EditText
    private lateinit var micBtn: ImageButton
    private lateinit var sendBtn: ImageButton
    private lateinit var toggleTheme: ImageButton
    private lateinit var historyBtn: ImageButton
    private lateinit var resultsPanel: LinearLayout
    private lateinit var resultText: TextView
    private lateinit var aiExplanation: TextView
    private lateinit var historyPanel: LinearLayout
    private lateinit var historyList: ListView

    // --- Calculator button references ---
    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btn8: Button
    private lateinit var btn9: Button
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button
    private lateinit var btnMultiply: Button
    private lateinit var btnDivide: Button
    private lateinit var btnEquals: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI element setup via findViewById
        root = findViewById(R.id.root)
        inputPanel = findViewById(R.id.inputPanel)
        inputField = findViewById(R.id.inputField)
        micBtn = findViewById(R.id.micBtn)
        sendBtn = findViewById(R.id.sendBtn)
        toggleTheme = findViewById(R.id.toggleTheme)
        historyBtn = findViewById(R.id.historyBtn)
        resultsPanel = findViewById(R.id.resultsPanel)
        resultText = findViewById(R.id.resultText)
        aiExplanation = findViewById(R.id.aiExplanation)
        historyPanel = findViewById(R.id.historyPanel)
        historyList = findViewById(R.id.historyList)

        // Calculator buttons setup: match new XML grid
        btn0 = findViewById(R.id.btn0)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btn8 = findViewById(R.id.btn8)
        btn9 = findViewById(R.id.btn9)
        btnPlus = findViewById(R.id.btnPlus)
        btnMinus = findViewById(R.id.btnMinus)
        btnMultiply = findViewById(R.id.btnMultiply)
        btnDivide = findViewById(R.id.btnDivide)
        btnEquals = findViewById(R.id.btnEquals)

        // Setup UI
        calculationHistory = mutableListOf()
        isDarkTheme = false // Default: light

        setupTheme(isDarkTheme)
        setupListeners()
        setupCalculatorButtonListeners()
        setupHistoryList()
    }

    /**
     * Attach click listeners for calculator buttons to insert symbol/number into input or perform calculation.
     */
    private fun setupCalculatorButtonListeners() {
        // Helper to insert text at cursor (replace selection, if any)
        fun insertTextAtCursor(s: String) {
            val start = inputField.selectionStart.coerceAtLeast(0)
            val end = inputField.selectionEnd.coerceAtLeast(0)
            inputField.text.replace(
                minOf(start, end),
                maxOf(start, end),
                s,
                0,
                s.length
            )
            inputField.setSelection(minOf(start, end) + s.length)
        }

        // Number buttons
        btn0.setOnClickListener { insertTextAtCursor("0") }
        btn1.setOnClickListener { insertTextAtCursor("1") }
        btn2.setOnClickListener { insertTextAtCursor("2") }
        btn3.setOnClickListener { insertTextAtCursor("3") }
        btn4.setOnClickListener { insertTextAtCursor("4") }
        btn5.setOnClickListener { insertTextAtCursor("5") }
        btn6.setOnClickListener { insertTextAtCursor("6") }
        btn7.setOnClickListener { insertTextAtCursor("7") }
        btn8.setOnClickListener { insertTextAtCursor("8") }
        btn9.setOnClickListener { insertTextAtCursor("9") }
        // Operator buttons: use standard calculator symbols in input logic
        btnPlus.setOnClickListener { insertTextAtCursor("+") }
        btnMinus.setOnClickListener { insertTextAtCursor("-") }
        btnMultiply.setOnClickListener { insertTextAtCursor("*") } // Still use '*' for calculation logic
        btnDivide.setOnClickListener { insertTextAtCursor("/") } // Use '/' for division logic
        btnEquals.setOnClickListener { sendBtn.performClick() }
    }

    /**
     * Set up the base colors and theme
     */
    private fun setupTheme(dark: Boolean) {
        if (dark) {
            root.setBackgroundColor(0xFF181818.toInt()) // near-black
            inputPanel.setBackgroundColor(0xFF222222.toInt())
            resultsPanel.setBackgroundColor(0xFF232323.toInt())
            historyPanel.setBackgroundColor(0xFF303030.toInt())
            inputField.setTextColor(0xFFFFFFFF.toInt())
            toggleTheme.setImageResource(R.drawable.ic_light_mode)
        } else {
            root.setBackgroundColor(0xFFF8F9FA.toInt()) // slight off-white
            inputPanel.setBackgroundColor(0xFFE9ECEF.toInt())
            resultsPanel.setBackgroundColor(0xFFFFFFFF.toInt())
            historyPanel.setBackgroundColor(0xFFF3F3F3.toInt())
            inputField.setTextColor(0xFF1A202C.toInt())
            toggleTheme.setImageResource(R.drawable.ic_dark_mode)
        }
        // Accent coloring
        resultText.setTextColor(PRIMARY_COLOR)
        aiExplanation.setTextColor(ACCENT_COLOR)
    }

    /**
     * Toggle dark/light theme
     */
    private fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        setupTheme(isDarkTheme)
    }

    /**
     * Set up listeners for all interactive UI elements.
     */
    private fun setupListeners() {
        // Query submission
        sendBtn.setOnClickListener {
            val input = inputField.text.toString()
            if (input.isNotBlank()) {
                processUserQuery(input)
                inputField.text.clear()
            }
        }
        // Voice input:
        micBtn.setOnClickListener {
            startVoiceInput()
        }
        // Theme toggle
        toggleTheme.setOnClickListener {
            toggleTheme()
        }
        // Open/close history panel
        historyBtn.setOnClickListener {
            toggleHistoryPanel()
        }
    }

    /**
     * Initialize and set up the history panel listview
     */
    private fun setupHistoryList() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, calculationHistory.map { it.displayText() })
        historyList.adapter = adapter
        historyList.setOnItemClickListener { _, _, pos, _ ->
            val item = calculationHistory[pos]
            fillFromHistory(item)
        }
    }

    /**
     * Fill input with previous calculation, on history tap.
     */
    private fun fillFromHistory(item: HistoryItem) {
        inputField.setText(item.userInput)
    }

    /**
     * Show/hide history panel
     */
    private fun toggleHistoryPanel() {
        if (historyPanel.visibility == View.VISIBLE)
            historyPanel.visibility = View.GONE
        else
            historyPanel.visibility = View.VISIBLE
    }

    /**
     * PUBLIC_INTERFACE
     * Main AI/Natural Language query submission handler.
     * Sends query to backend and updates result display, history etc.
     */
    private fun processUserQuery(query: String) {
        // 1. (Here you would call your backend. For now: dummy logic.)
        val (result, explanation) = evaluateExpression(query)
        showResult(result, explanation)
        addToHistory(query, result, explanation)
    }

    /**
     * Show calculation result and AI explanation in the results panel.
     */
    private fun showResult(result: String, explanation: String) {
        resultText.text = result
        aiExplanation.text = explanation
    }

    /**
     * Add calculation to history and update list adapter.
     */
    private fun addToHistory(input: String, result: String, explanation: String) {
        calculationHistory.add(0, HistoryItem(input, result, explanation))
        setupHistoryList()
    }

    /**
     * Basic/dummy expression evaluation until backend API is available.
     * Supports basic math and very simple natural language (for showcase).
     */
    private fun evaluateExpression(expr: String): Pair<String, String> {
        // This should call your backend via Retrofit/Volley; here: demo logic.
        return when {
            expr.contains("plus", true) || expr.contains("+") -> {
                val nums = expr.split(Regex("[^0-9]+")).mapNotNull { it.toIntOrNull() }
                val sum = nums.sum()
                Pair(sum.toString(), "AI: Sum computed by adding ${nums.joinToString(" + ")}.")
            }
            expr.contains("multiply", true) || expr.contains("*") -> {
                val nums = expr.split(Regex("[^0-9]+")).mapNotNull { it.toIntOrNull() }
                val prod = nums.fold(1) { acc, n -> acc * n }
                Pair(prod.toString(), "AI: Product computed by multiplying ${nums.joinToString(" × ")}.")
            }
            expr.contains("square root", true) -> {
                val n = expr.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                Pair("%.4f".format(Math.sqrt(n)), "AI: Square root of $n is shown above.")
            }
            expr.contains("power", true) || expr.contains("^") -> {
                val nums = expr.split(Regex("[^0-9]+")).mapNotNull { it.toDoubleOrNull() }
                if (nums.size >= 2)
                    Pair("%.3f".format(Math.pow(nums[0], nums[1])), "AI: ${nums[0]} raised to power ${nums[1]}")
                else
                    Pair("?", "AI: Could not parse exponentiation.")
            }
            else -> {
                // Default: echo query or fallback string
                Pair("?", "AI: Natural language understanding will be connected to backend in production build.")
            }
        }
    }

    // --- Voice Recognition Integration ---

    /**
     * Request permission and start Android SpeechRecognizer
     */
    private fun startVoiceInput() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return
        }
        val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your calculation")
        }
        SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : android.speech.RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {
                    Toast.makeText(this@MainActivity, "Mic error: $error", Toast.LENGTH_SHORT).show()
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val voiceInput = matches[0]
                        inputField.setText(voiceInput)
                        processUserQuery(voiceInput)
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            startListening(intent)
        }
    }

    // --- Models ---
    /**
     * Represents an entry in calculation history.
     */
    data class HistoryItem(
        val userInput: String,
        val result: String,
        val explanation: String
    ) {
        fun displayText(): String = "$userInput = $result"
    }

    // PUBLIC_INTERFACE
    public fun openSettingsScreen() {
        // stub - would launch settings activity for app options in multi-screen app
        Toast.makeText(this, "Settings not available in demo", Toast.LENGTH_SHORT).show()
    }
}
